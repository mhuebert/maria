(ns maria.views.icons)

(defn size [icon size]
  (update icon 1 assoc :width size :height size))

(defn style [icon styles]
  (apply update-in icon [1 :style] merge styles))

(defn class [icon class]
  (update-in icon [1 :class] str " " class))

(def Add
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ArrowDropDown
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M7 10l5 5 5-5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ArrowDropUp
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M7 14l5-5 5 5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Backup
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"}]])

(def Build
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:clip-rule "evenodd", :d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z"}]])

(def Cancel
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Code
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0V0z", :fill "none"}]
   [:path {:d "M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z"}]])

(def ContentDuplicate
  [:svg {:fill "currentColor", :xmlns "http://www.w3.org/2000/svg", :version "1.1", :width "24", :height "24", :view-box "0 0 24 24"}
   [:path {:d "M11,17H4A2,2 0 0,1 2,15V3A2,2 0 0,1 4,1H16V3H4V15H11V13L15,16L11,19V17M19,21V7H8V13H6V7A2,2 0 0,1 8,5H19A2,2 0 0,1 21,7V21A2,2 0 0,1 19,23H8A2,2 0 0,1 6,21V19H8V21H19Z"}]])

(def ContentPaste
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M19 2h-4.18C14.4.84 13.3 0 12 0c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm7 18H5V4h2v3h10V4h2v16z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ContentCopy
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"}]])

(def CursorText
  [:svg {:fill "currentColor"  :xml:space "preserve", :width "24px", :view-box "5.5 5.5 24 24", :xmlns "http://www.w3.org/2000/svg",:height "24px"}
   [:g {:id "rotation", :transform "translate(17.500000, 17.000000) rotate(-270.000000) translate(-17.500000, -17.000000) translate(9.500000, 13.500000)"} "\t\t\t"
    [:path {:id "stem", :d "M1.975,3.994h11.993V3.008H1.975V3.994z"}]
    [:path {:id "stem-dash", :d "M7.976,4.996H8.96V2.01H7.976V4.996z"}]
    [:path {:id "curve4", :fill "none", :stroke "#000000", :d "M2.713,3.494c-0.5-0.167-1.27-0.737-1.525-1.107\n\t\t\t\tc-0.243-0.35-0.45-0.71-0.559-1.054C0.494,0.912,0.535,0.025,0.535,0.025"}] "\t\t\t"
    [:path {:id "curve3", :fill "none", :stroke "#000000", :d "M2.715,3.528C2.19,3.681,1.385,4.312,1.166,4.593\n\t\t\t\tc-0.26,0.336-0.428,0.709-0.537,1.053C0.494,6.068,0.535,6.955,0.535,6.955"}] "\t\t\t"
    [:path {:id "curve2", :fill "none", :stroke "#000000", :d "M13.26,3.494c0.5-0.167,1.269-0.737,1.523-1.107\n\t\t\t\tc0.244-0.35,0.451-0.71,0.558-1.054c0.136-0.421,0.095-1.308,0.095-1.308"}] "\t\t\t"
    [:path {:id "curve1", :fill "none", :stroke "#000000", :d "M13.256,3.528c0.525,0.153,1.329,0.784,1.549,1.065\n\t\t\t\tc0.261,0.336,0.429,0.709,0.537,1.053c0.136,0.422,0.095,1.309,0.095,1.309"}] "\t\t"] "\t"])

(def ExpandMore
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])


(def FormatItalic
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"}]])

(def FormatBold
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M15.6 10.79c.97-.67 1.65-1.77 1.65-2.79 0-2.26-1.75-4-4-4H7v14h7.04c2.09 0 3.71-1.7 3.71-3.79 0-1.52-.86-2.82-2.15-3.42zM10 6.5h3c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5h-3v-3zm3.5 9H10v-3h3.5c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def FormatIndent
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M3 21h18v-2H3v2zM3 8v8l4-4-4-4zm8 9h10v-2H11v2zM3 3v2h18V3H3zm8 6h10V7H11v2zm0 4h10v-2H11v2z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def FormatOutdent
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M11 17h10v-2H11v2zm-8-5l4 4V8l-4 4zm0 9h18v-2H3v2zM3 3v2h18V3H3zm8 6h10V7H11v2zm0 4h10v-2H11v2z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def FormatListBulleted
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M4 10.5c-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5-.67-1.5-1.5-1.5zm0-6c-.83 0-1.5.67-1.5 1.5S3.17 7.5 4 7.5 5.5 6.83 5.5 6 4.83 4.5 4 4.5zm0 12c-.83 0-1.5.68-1.5 1.5s.68 1.5 1.5 1.5 1.5-.68 1.5-1.5-.67-1.5-1.5-1.5zM7 19h14v-2H7v2zm0-6h14v-2H7v2zm0-8v2h14V5H7z"}]
   [:path {:d "M0 0h24v24H0V0z", :fill "none"}]])

(def FormatListOrdered
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M2 17h2v.5H3v1h1v.5H2v1h3v-4H2v1zm1-9h1V4H2v1h1v3zm-1 3h1.8L2 13.1v.9h3v-1H3.2L5 10.9V10H2v1zm5-6v2h14V5H7zm0 14h14v-2H7v2zm0-6h14v-2H7v2z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def FormatSize
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M9 4v3h5v12h3V7h5V4H9zm-6 8h3v7h3v-7h3V9H3v3z"}]])

(def FormatQuote
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M6 17h3l2-4V7H5v6h3zm8 0h3l2-4V7h-6v6h3z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Home
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ModeEdit
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Undo
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M12.5 8c-2.65 0-5.05.99-6.9 2.6L2 7v9h9l-3.62-3.62c1.39-1.16 3.16-1.88 5.12-1.88 3.54 0 6.55 2.31 7.6 5.5l2.37-.78C21.08 11.03 17.15 8 12.5 8z"}]])

(def Redo (update Undo 1 assoc :style {:transform "scaleX(-1)"}))

(def Select
  [:svg {:fill "currentColor" :width "24px", :view-box "0 0 24 24", :xmlns "http://www.w3.org/2000/svg", :height "24px"}
   [:path {:fill "none", :d "M0,0h24v24H0V0z"}]
   [:path {:d "M3,5h2V3C3.9,3,3,3.9,3,5z M3,13h2v-2H3V13z M7,21h2v-2H7V21z M3,9h2V7H3V9z M13,3h-2v2h2V3z M19,3v2h2C21,3.9,20.1,3,19,3z\n\t M5,21v-2H3C3,20.1,3.9,21,5,21z M3,17h2v-2H3V17z M9,3H7v2h2V3z M11,21h2v-2h-2V21z M19,13h2v-2h-2V13z M19,21c1.1,0,2-0.9,2-2h-2\n\tV21z M19,9h2V7h-2V9z M19,17h2v-2h-2V17z M15,21h2v-2h-2V21z M15,5h2V3h-2V5z"}]])

(def SelectAll
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M3 5h2V3c-1.1 0-2 .9-2 2zm0 8h2v-2H3v2zm4 8h2v-2H7v2zM3 9h2V7H3v2zm10-6h-2v2h2V3zm6 0v2h2c0-1.1-.9-2-2-2zM5 21v-2H3c0 1.1.9 2 2 2zm-2-4h2v-2H3v2zM9 3H7v2h2V3zm2 18h2v-2h-2v2zm8-8h2v-2h-2v2zm0 8c1.1 0 2-.9 2-2h-2v2zm0-12h2V7h-2v2zm0 8h2v-2h-2v2zm-4 4h2v-2h-2v2zm0-16h2V3h-2v2zM7 17h10V7H7v10zm2-8h6v6H9V9z"}]])

(def Search
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Blank
  [:svg {:width "24" :height "24" :view-box "0 0 24 24" :xmlns "http://www.w3.org/2000/svg"}])