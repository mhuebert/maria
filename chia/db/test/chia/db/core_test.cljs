(ns chia.db.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [chia.db.core :as d :include-macros true]
            [chia.reactive :as r])
  (:require-macros [chia.db.test-helpers :refer [throws]]))

(deftest basic
  (let [db (d/create {})
        tx-log (atom [])]

    (is (satisfies? cljs.core/IDeref db)
        "DB is an atom")

    (d/listen db (fn [{:keys [::d/datoms]}]
                   (swap! tx-log conj datoms)))

    (d/transact! db [{:db/id "herman"}])

    (is (false? (d/contains? db "herman"))
        "Inserting an entity without attributes is no-op")

    (d/transact! db [{:db/id "herman" :occupation "teacher"}])

    (is (= (last @tx-log)
           [["herman" :occupation "teacher" nil]])
        "Tx-log listener called with datoms")

    (is (= {:db/id "herman" :occupation "teacher"} (d/entity db "herman"))
        "Entity is returned as it was inserted")

    (is (= "herman" (:db/id (d/entity db "herman")))
        "Entity is returned with :db/id attribute")

    (is (= "teacher" (d/get db "herman" :occupation))
        "d/get an attribute of an entity")

    (is (= 1 (count (d/entities db [[:occupation "teacher"]])))
        "Query on non-indexed attr")

    (d/transact! db [{:db/id "fred" :occupation "teacher"}])

    (is (= 2 (count (d/entities db [[:occupation "teacher"]])))
        "Verify d/insert! and query on non-indexed field")

    (d/transact! db [[:db/retract-attr "herman" :occupation]])

    (is (nil? (d/get db "herman" :occupation))
        "Retract attribute")

    (is (= 1 (count (d/entities db [[:occupation "teacher"]])))
        "Retract non-indexed field")

    (d/transact! db [[:db/retract-attr "herman" :id]])
    (is (nil? (d/entity db "herman"))
        "Entity with no attributes is removed")

    (is (false? (contains? (get-in db [:ave :id]) "herman"))
        "Index has been removed")

    (d/transact! db [{:db/id "me"
                      :dog   "herman"
                      :name  "Matt"}])
    (d/transact! db [{:db/id "me"
                      :dog   nil}])

    (is (= (d/entity db "me") {:db/id "me" :name "Matt"})
        "Setting a value to nil is equivalent to retracting it")

    (is (= :error (try (d/transact! db [[:db/add "fred" :db/id "some-other-id"]])
                       nil
                       (catch js/Error e :error)))
        "Cannot change :db/id of entity")))

(deftest lookup-refs
  (let [db (-> (d/create {:email {:db/index :db.index/unique}})
               (d/transact! [{:db/id "fred"
                              :email "fred@example.com"}]))]

    (is (= (d/entity db "fred")
           (d/entity db [:email "fred@example.com"]))
        "Can substitute unique attr for id (à la 'lookup refs')")))

(deftest refs
  (let [db (-> (d/create {:owner {:db/type :db.type/ref}})
               (d/transact! [{:db/id "fred"
                              :name  "Fred"}
                             {:db/id "ball"
                              :name  "Ball"
                              :owner "fred"}]))]
    (is (= {:db/id  "fred"
            :name   "Fred"
            :_owner #{"ball"}} (d/touch db (d/entity db "fred")))
        "touch adds refs to entity"))

  (let [db (-> (d/create {:authors {:db/type        :db.type/ref
                                    :db/cardinality :db.cardinality/many}})
               (d/transact! [{:db/id "fred"
                              :name  "Fred"}
                             {:db/id "mary"
                              :name  "Mary"}
                             {:db/id   "1"
                              :name    "One"
                              :authors #{"fred" "mary"}}]))]
    (is (= {:db/id    "fred"
            :name     "Fred"
            :_authors #{"1"}} (d/touch db (d/entity db "fred")))
        "refs with cardinality-many")))

(deftest cardinality-many
  (let [db (-> (d/create {:db/id    {:db/index :db.index/unique}
                          :children {:db/cardinality :db.cardinality/many
                                     :db/index       true}})
               (d/transact! [{:db/id    "fred"
                              :children #{"pete"}}]))]

    (is (true? (contains? (get-in @db [:ave :children]) "pete"))
        "cardinality/many attribute can be indexed")

    ;; second child
    (d/transact! db [[:db/add "fred" :children #{"sally"}]])

    (is (= #{"sally" "pete"} (d/get db "fred" :children))
        "cardinality/many attribute returned as set")

    (is (= #{"fred"}
           (d/entity-ids db [[:children "sally"]])
           (d/entity-ids db [[:children "pete"]]))
        "look up via cardinality/many index")

    (testing "remove value from cardinality/many attribute"
      (d/transact! db [[:db/retract-attr "fred" :children #{"sally"}]])

      (is (= #{} (d/entity-ids db [[:children "sally"]]))
          "index is removed on retraction")
      (is (= #{"fred"} (d/entity-ids db [[:children "pete"]]))
          "index remains for other value")
      (is (= #{"pete"} (d/get db "fred" :children))
          "attribute has correct value"))



    (testing "unique attrs, duplicates"

      (d/merge-schema! db {:ssn  {:db/index :db.index/unique}
                           :pets {:db/cardinality :db.cardinality/many
                                  :db/index       :db.index/unique}})


      ;; cardinality single
      (d/transact! db [[:db/add "fred" :ssn "123"]])
      (is (= "fred" (:db/id (d/entity db [:ssn "123"]))))
      (throws (d/transact! db [[:db/add "herman" :ssn "123"]])
              "Cannot have two entities with the same unique attr")

      ;; cardinality many
      (d/transact! db [[:db/add "fred" :pets #{"fido"}]])
      (is (= "fred" (:db/id (d/entity db [:pets "fido"]))))
      (throws (d/transact! db [[:db/add "herman" :pets #{"fido"}]])
              "Two entities with same unique :db.cardinality/many attr")
      (throws (d/transact! db [{:db/id "herman"
                                :pets  #{"fido"}}])
              "Two entities with same unique :db.cardinality/many attr"))))

(deftest pattern-listeners
  (let [db (d/create {:person/children {:db/cardinality :db.cardinality/many
                                        :db/index       :db.index/unique}})
        log (atom {})
        reader-1 (fn [path]
                   (fn [{:keys [::d/datoms]}]
                     (swap! log assoc path datoms)))]

    (testing "entity pattern"
      (d/transact! db [{:db/id "mary"
                        :name  "Mary"}
                       [:db/add "mary"
                        :person/children #{"john"}]
                       {:db/id "john"
                        :name  "John"}])

      (is (= "Mary" (d/get db [:person/children "john"] :name))
          "Get attribute via lookup ref")

      (d/listen db (reader-1 :mary-entity) {:e__ ["mary"]})

      (d/transact! db [[:db/add "mary" :name "MMMary"]])

      (is (= [["mary" :name "MMMary" "Mary"]] (:mary-entity @log))
          "Entity listener called when attribute changes"))

    (testing "lookup ref pattern"

      (d/transact! db [{:db/id "peter" :name "Peter"}])

      (is (do
            (r/with-dependency-tracking! {:reader reader-1}
                                         (d/entity db [:person/children "peter"]))
            (-> (get-in @r/dependencies [reader-1 db])
                (= {:_av #{[:person/children "peter"]}})))
          "Lookup ref logs attr-val listener when target entity does not exist")

      (d/transact! db [[:db/add "mary" :person/children #{"peter"}]])

      (is (do
            (r/with-dependency-tracking! {:reader reader-1}
                                         (d/entity db [:person/children "peter"]))
            (-> (get-in @r/dependencies [reader-1 db])
                (= {:_av #{[:person/children "peter"]}
                    :e__ #{"mary"}})))
          "Lookup ref logs attr-val & entity listeners when entity exists")

      (let [reader-2 (let [state (atom {:invalidations 0
                                        :renders       0})]
                       (reify
                         IDeref
                         (-deref [_] @state)
                         r/IRecompute
                         (-recompute! [this]
                           (swap! state update :invalidations inc)
                           (this))
                         IFn
                         (-invoke [this]
                           (swap! state update :renders inc)
                           (r/with-dependency-tracking! {:reader this}
                                                        (d/entity db [:person/children "peter"])))))
            reader-dependencies #(-> @r/dependencies
                                     (get-in [reader-2 db]))
            log! #(prn % {:reader       @reader-2
                          :dependencies (reader-dependencies)})]

        (reader-2)

        (is (= @reader-2
               {:invalidations 0
                :renders       1}))

        (is (-> (reader-dependencies)
                :e__
                (contains? "mary")))

        (d/transact! db [[:db/retract-attr "mary" :person/children #{"peter"}]])

        (is (= @reader-2
               {:invalidations 1
                :renders       2}))

        (is (-> (reader-dependencies)
                :e__
                (contains? "mary")
                (not)))))))