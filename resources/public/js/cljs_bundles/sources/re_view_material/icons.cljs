(ns re-view-material.icons)

(defn size [icon size]
  (update icon 1 assoc :width size :height size))

(defn styles [icon & args]
  (apply update-in icon [1 :style] assoc args))

(defn class [icon class]
  (update-in icon [1 :class] str " " class))

(def ArrowBack
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"}]])

(def ArrowCompress
  ;; https://materialdesignicons.com/icon/arrow-compress
  [:svg {:fill "currentColor", :xmlns "http://www.w3.org/2000/svg", :width "24", :height "24", :view-box "0 0 24 24"}
   [:path {:d "M19.5,3.09L15,7.59V4H13V11H20V9H16.41L20.91,4.5L19.5,3.09M4,13V15H7.59L3.09,19.5L4.5,20.91L9,16.41V20H11V13H4Z"}]])

(def ArrowDropDown
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M7 10l5 5 5-5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ArrowDropUp
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M7 14l5-5 5 5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ArrowExpand
  ;; https://materialdesignicons.com/icon/arrow-expand
  [:svg {:fill "currentColor", :xmlns "http://www.w3.org/2000/svg", :width "24", :height "24", :view-box "0 0 24 24"}
   [:path {:d "M10,21V19H6.41L10.91,14.5L9.5,13.09L5,17.59V14H3V21H10M14.5,10.91L19,6.41V10H21V3H14V5H17.59L13.09,9.5L14.5,10.91Z"}]])

(def Cancel
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ChangeHistory
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M12 7.77L18.39 18H5.61L12 7.77M12 4L2 20h20L12 4z"}]
   [:path {:d "M0 0h24v24H0V0z", :fill "none"}]])
(def Close
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def Code
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0V0z", :fill "none"}]
   [:path {:d "M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z"}]])

(def ModeEdit
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ExpandLess
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M12 8l-6 6 1.41 1.41L12 10.83l4.59 4.58L18 14z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ExpandMore
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def FileUpload
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M9 16h6v-6h4l-7-7-7 7h4zm-4 2h14v2H5z"}]])

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

(def Menu
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M0 0h24v24H0z", :fill "none"}]
   [:path {:d "M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"}]])

(def Search
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def StarRate
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 18 18", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M9 11.3l3.71 2.7-1.42-4.36L15 7h-4.55L9 2.5 7.55 7H3l3.71 2.64L5.29 14z"}]
   [:path {:d "M0 0h18v18H0z", :fill "none"}]])