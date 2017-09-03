(ns balls.core
    (:require [reagent.core :as r :refer [atom]]
              [cljsjs.react-pixi]
              [cljs.core.async :refer [<! >! chan]])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defonce pixi js/ReactPIXI)

(enable-console-print!)

(defonce Stage (r/adapt-react-class pixi.Stage))
(defonce Container (r/adapt-react-class pixi.DisplayObjectContainer))
(defonce Sprite (r/adapt-react-class pixi.Sprite))

(def t (atom 0))
(def time-chan (chan))

(defn current-page []
  [Stage {:width 640
          :height 480}
   [Sprite {:image "img/layer5.png"
            :x 0
            :y 0
            :width 640
            :height 480}]
   [Sprite {:image "img/layer1.png"
            :x @t
            :y 0
            :width 640
            :height 480}]])

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn tick []
  (go (>! time-chan :tick))
  (.requestAnimationFrame js/window tick))

(defn init! []
  (mount-root)
  (tick))

(go-loop [game {:update inc}]
  (let [evt (<! time-chan)]
    (swap! t (game :update))
    (cond
      (> @t 640) (recur (assoc game :update dec))
      (< @t 0)   (recur (assoc game :update inc))
      :default   (recur game))))
