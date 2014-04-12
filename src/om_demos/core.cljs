(ns om-demos.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.date :as dt]))

(enable-console-print!)

(def app-state (atom {:time (goog.date.DateTime.)}))

(defn clock [data owner {:keys [time-key formatter tick-fn]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (tick-fn data))
    om/IRender
    (render [_]
      (dom/div nil
               (formatter data)))))

(defn formatter [data]
  (.toIsoTimeString (:time data)))

(defn tick [data]
  (js/setInterval
    (fn [] (om/update! data {:time (doto (:time @data)
                                     (.add (goog.date.Interval. 0 0 0 0 0 1)))}))
    1000))

(om/root
  (fn [app owner]
    (om/build clock app {:opts {:time-key :time
                                :formatter formatter
                                :tick-fn tick}}))
  app-state
  {:target (. js/document (getElementById "app"))})
