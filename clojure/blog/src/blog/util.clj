(ns blog.util
  (:require [garden.core :refer [css]]
            [garden.units :refer [px percent] :rename {percent %}]
            [garden.stylesheet :refer [at-media]]))

(def clearfix
  [:&:before, :&:after
   {:clear :both
    :display :table
    :content "' '"}])

(def clear-fix clearfix)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Breakpoints

(defmacro defbreakpoint [name media-params]
  `(defn ~name [& rules#]
     (at-media ~media-params
       [:& rules#])))

(defbreakpoint small-screen
  {:screen true
   :min-width (px 320)
   :max-width (px 480)})

;; iPhone 2G, 3G, 4, 4S

(def iphone-media-params
  {:screen true
   :min-device-width (px 320)
   :max-device-width (px 480)})

(defbreakpoint iPhone
  iphone-media-params)

(defbreakpoint iphone-landscape
  (assoc iphone-media-params :orientation :landscape))

(defbreakpoint iphone-portrait
  (assoc iphone-media-params :orientation :portrait))

;; iPhone 5

(def iphone-5-media-params
  {:screen true
   :min-device-width (px 320)
   :max-device-width (px 568)})

(defbreakpoint iphone-5
  iphone-5-media-params)

(defbreakpoint iphone-5-landscape
  (assoc iphone-5-media-params :orientation :landscape))

(defbreakpoint iphone-5-portrait
  (assoc iphone-5-media-params :orientation :portrait))

(defbreakpoint medium-screen
  {:screen true
   :min-width (px 481)})

(defbreakpoint medium-screen-landscape
  {:screen true
   :min-width (px 768)
   :max-width (px 1024)
   :orientation :landscape})

(defbreakpoint medium-screen-portrait
  {:screen true
   :min-width (px 768)
   :max-width (px 1024)
   :orientation :portrait})

(defbreakpoint large-screen
  {:screen true
   :min-width (px 1024)})

(defbreakpoint x-large-screen
  {:screen true
   :min-width (px 1824)})

(def container
  (list
   [:& clear-fix]
   (small-screen
    [:& {:max-width (px 480)}])
   (medium-screen
     [:& {:max-width (px 760)}])
   (large-screen
    [:& {:max-width (px 1224)}])
   (x-large-screen
     [:& {:max-width (px 1824)}])))

(defn offset [n]
  (let [m (min (max 0 n) 12)]
    [:&
     {:margin-left (% (* 100 (/ n 12.0)))}]))

(defn col [n]
  (let [m (min (max 0 n) 12)]
    [:&
     {:float :left
      :position :relative
      :min-height (px 1)
      :padding-left (px 15)
      :padding-right (px 15)
      :width (% (* 100 (/ m 12.0)))}]))

(def row
  [:& 
   {:margin-right (px -15)
    :margin-left (px -15)}
   clearfix])


(defn whole-number? [n]
  (= n (Math/floor n)))

(defn modular-scale-fn [base ratio]
  (let [[up down] (if (ratio? ratio)
                    (if (< (denominator ratio)
                           (numerator ratio))
                      [* /]
                      [/ *])
                    (if (< 1 ratio)
                      [* /]
                      [/ *]))
        f (float ratio)
        us (iterate #(up % f) base)
        ds (iterate #(down % f) base)]
    (memoize
     (fn ms [n]
       (cond
        (< 0 n) (if (whole-number? n)
                  (nth us n)
                  (let [m (Math/floor (float n))
                        [a b] [(ms m) (ms (inc m))]]
                    (+ a (* (Math/abs (- a b))
                            (- n m)))))
        (< n 0) (if (whole-number? n)
                  (nth ds (Math/abs n))
                  (let [m (Math/floor (float n))
                        [a b] [(ms m) (ms (dec m))]]
                    (+ a (* (Math/abs (- a b))
                            (- n m)))))
        :else base)))))
