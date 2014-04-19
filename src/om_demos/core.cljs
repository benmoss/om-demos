(ns om-demos.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.date :as gdate]
            [goog.events :as gevents]
            [goog.string :as gstring]
            [goog.string.format]))

(enable-console-print!)

(defn formatter [{:keys [time]}]
  (let [hours (.getHours time)
        ampm-hours (if (> hours 12) (- hours 12) hours)]
    [{:value ampm-hours} {:value (.getMinutes time)} {:value (.getSeconds time)}]))

(defn subclock [data owner {:keys [tick-fn interval]}]
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
    om/IRenderState
    (render-state [_ state]
      (let [prior (:prior-val state)
            class-names (cond-> ["time"]
                          (:animating state) (conj "animating"))
            class-names (apply str (interpose " " class-names))]
        (dom/div #js {:className class-names}
                 (dom/div #js {:ref "container"}
                          (dom/div #js {:className "next"} (gstring/format "%02d" (:value data)))
                          (dom/div #js {:className "current"} (when prior (gstring/format "%02d" (:value prior))))))))))

(defn tick [data]
  (om/update! data {:time (doto (goog.date.DateTime. (:time @data))
                            (.add (goog.date.Interval. 0 0 0 0 0 1)))}))

(defn clock [app owner {:keys [tick-fn interval]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (js/setInterval (partial tick-fn app) interval))
    om/IRender
    (render [_]
      (let [[hour minute seconds] (formatter app)]
        (dom/div #js {:id "topnav"}
                 (dom/div #js {:id "main"}
                          (dom/div #js {:id "now-demo"}
                                   (om/build subclock hour)
                                   ": "
                                   (om/build subclock minute)
                                   ": "
                                   (om/build subclock seconds))))))))

(def app-state (atom {:time (goog.date.DateTime.)}))

(om/root
  clock
  app-state
  {:target (. js/document (getElementById "app"))
   :opts {:interval 1000
          :tick-fn tick}})
