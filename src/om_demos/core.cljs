(ns om-demos.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.date :as gdate]
            [goog.events :as gevents]))

(enable-console-print!)

(defn clock [data owner {:keys [formatter tick-fn interval]}]
  (reify
    om/IInitState
    (init-state [_]
      {:animating false})
    om/IWillUpdate
    (will-update [_ next-props next-state]
      (when (not= (om/get-props owner) next-props)
        (.listenOnce goog.events (om/get-node owner "container") "webkitAnimationEnd" #(om/set-state! owner :animating false))
        (om/set-state! owner :prior-val (om/get-props owner))
        (om/set-state! owner :animating true)))
    om/IWillMount
    (will-mount [_]
      (js/setInterval (partial tick-fn data) interval))
    om/IRenderState
    (render-state [_ state]
      (let [prior (:prior-val state)
            class-names (cond-> ["time"]
                          (:animating state) (conj "animating"))
            class-names (apply str (interpose " " class-names))]
        (dom/div #js {:className class-names}
                 (dom/div #js {:ref "container"}
                          (dom/div #js {:className "next"} (formatter data))
                          (dom/div #js {:className "current"} (when prior (formatter prior)))))))))

(defn formatter [data]
  (.toIsoTimeString (:time data)))

(defn tick [data]
  (om/update! data {:time (doto (goog.date.DateTime. (:time @data))
                            (.add (goog.date.Interval. 0 0 0 0 0 1)))}))

(defn app-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "topnav"}
      (dom/div #js {:id "main"}
      (dom/div #js {:id "now-demo"}
               (om/build clock app {:opts {:interval 1000
                                           :formatter formatter
                                           :tick-fn tick}})))))))

(def app-state (atom {:time (goog.date.DateTime.)}))

(om/root
  app-view
  app-state
  {:target (. js/document (getElementById "app"))})
