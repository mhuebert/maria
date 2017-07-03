(ns re-view-hiccup.react.attrs)

;; identify *dashed* attributes (to camelCase).
;; (not specifically handled by React should not be camelCased.)

;; canonical source of attributes handled by React:
;; https://facebook.github.io/react/docs/dom-elements.html

(def html-attrs
  [:accept-charset :access-key :allow-full-screen :allow-transparency :auto-complete :auto-focus :auto-play :cell-padding :cell-spacing :char-set :classID :class-name :col-span :content-editable :context-menu :cross-origin :date-time :enc-type :form-action :form-enc-type :form-method :form-no-validate :form-target :frame-border :href-lang :html-for :http-equiv :input-mode :key-params :key-type :margin-height :margin-width :max-length :media-group :min-length :no-validate :radio-group :read-only :row-span :spell-check :src-doc :src-lang :src-set :tab-index :use-map])

(def input-attrs
  [:default-value :default-checked])

(def non-standard-attrs
  [:auto-capitalize :auto-correct :item-prop :item-scope :item-type :item-ref :itemID :auto-save])

(def svg-attrs
  [:accent-height :alignment-baseline :allow-reorder :arabic-form :attribute-name :attribute-type :auto-reverse :base-frequency :base-profile :baseline-shift :calc-mode :cap-height :clip-path :clip-path-units :clip-rule :color-interpolation :color-interpolation-filters :color-profile :color-rendering :content-script-type :content-style-type :diffuse-constant :dominant-baseline :edge-mode :enable-background :external-resources-required :fill-opacity :fill-rule :filter-res :filter-units :flood-color :flood-opacity :font-family :font-size :font-size-adjust :font-stretch :font-style :font-variant :font-weight :glyph-name :glyph-orientation-horizontal :glyph-orientation-vertical :glyph-ref :gradient-transform :gradient-units :horiz-advX :horiz-originX :image-rendering :kernel-matrix :kernel-unit-length :key-points :key-splines :key-times :length-adjust :letter-spacing :lighting-color :limiting-cone-angle :marker-end :marker-height :marker-mid :marker-start :marker-units :marker-width :mask-content-units :mask-units :num-octaves :overline-position :overline-thickness :paint-order :path-length :pattern-content-units :pattern-transform :pattern-units :pointer-events :points-atX :points-atY :points-atZ :preserve-alpha :preserve-aspect-ratio :primitive-units :refX :refY :rendering-intent :repeat-count :repeat-dur :required-extensions :required-features :shape-rendering :specular-constant :specular-exponent :spread-method :start-offset :std-deviation :stitch-tiles :stop-color :stop-opacity :strikethrough-position :strikethrough-thickness :stroke-dasharray :stroke-dashoffset :stroke-linecap :stroke-linejoin :stroke-miterlimit :stroke-opacity :stroke-width :surface-scale :system-language :table-values :targetX :targetY :text-anchor :text-decoration :text-length :text-rendering :underline-position :underline-thickness :unicode-bidi :unicode-range :units-per-em :v-alphabetic :v-hanging :v-ideographic :v-mathematical :vector-effect :vert-advY :vert-originX :vert-originY :view-box :view-target :word-spacing :writing-mode :x-channel-selector :x-height :xlink-actuate :xlink-arcrole :xlink-href :xlink-role :xlink-show :xlink-title :xlink-type :xmlns-xlink :xml-base :xml-lang :xml-space :y-channel-selector :zoom-and-pan])

(def attrs (->> [html-attrs
                 input-attrs
                 non-standard-attrs
                 svg-attrs]
                (apply concat)
                (set)))