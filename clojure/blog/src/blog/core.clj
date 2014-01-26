(ns blog.core
  (:refer-clojure :exclude [+ - * / comment keyword])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [garden.def :refer [defrule defcssfn defstyles defstylesheet]]
            [garden.units :refer [px em percent] :rename {percent %}]
            [garden.color :as color :refer [hsl]]
            [garden.stylesheet :refer [at-import]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.repl]))

(def STYLESHEET-PATH
  (.. (io/file "../../css/")
      getCanonicalPath))

(def MAIN-STYLESHEET-PATH
  (.. (io/file STYLESHEET-PATH "main.css")
      getPath))

(def SYNTAX-STYLESHEET-PATH
  (.. (io/file STYLESHEET-PATH "syntax.css")
      getPath))

;; Utilities

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

(defn fonts [& fonts]
  (map
   (fn [font]
     (if (and (string? font)
              (not= (.indexOf font " ") -1))
       (pr-str font)
       font))
   fonts))

;;;; Vars

(def ms
  (let [f (modular-scale-fn 16 5/4)]
    (fn [n]
      (px (f n)))))

(def background-color (hsl 58 70 98))
(def text-color (hsl 83 36 17))
(def anchor-color (hsl 19 54 45))

(def heading-font
  (fonts "Poiret One" "sans-serif"))

(def copy-font
  (fonts "Gentium Basic" "serif"))

(def code-font
  (fonts "Courier" "monospace"))

;; Rules

(defrule a :a)
(defrule on-hover :&:hover)
(defrule visited? :&:visited)
(defrule pre :pre)
(defrule code :code)
(defrule blockquote :blockquote)

(defrule site-title :.site-title)
(defrule post :.post)
(defrule post-meta :.post-meta)
(defrule post-date :.post-date)
(defrule post-title :.post-title)
(defrule post-content :.post-content)

;;;; Reset

(defstyles reset
  [:* {:margin 0 :padding 0}]

  [:*, :*:before, :*:after
   ^:prefix
   {:box-sizing "border-box"}]

  [:html, :body
   {:height (% 100)}]
  
  (a {:text-decoration "none"}))

;;;; Layout

(defstyles layout
  [:.site
   {:width (px 640)
    :margin [[0 "auto"]]}]

  (blockquote
   {:padding-left (ms 0)})

  (post-meta
   {:padding [[(ms -3) 0]]
    :margin [[(ms 0) 0]]})

  (post-title
   {:padding [[(ms 2) 0]]})

  (post-content
   [:p
    {:margin [[(ms 1) 0]]}]

   (pre
    {:padding [[(ms 1) 0]]})))

;;;; Typography

(defstyles typography
  [:body
   {:font-family copy-font}]

  (blockquote
   {:font-family copy-font
    :font-size (ms 1)
    :font-style :italic})

  (site-title
   {:text-transform :uppercase}
   (a
    {:color :inherit}))

  (post-date
   {:font-family copy-font
    :font-size (ms -1)
    :line-height (ms 1)
    :letter-spacing (px 1)})

  (post-title
   {:font-family heading-font 
    :font-size (ms 6)
    :font-weight 100
    :line-height (ms 6)})

  (post-content
   {:font-family copy-font
    :font-size (ms 0)
    :line-height (ms 2)}

   [:p
    {:text-align :justify}]

   (pre
    {:line-height (ms 1)}))

  (code
   {:font-family code-font
    :font-size (ms -0.5)}))

;;;; Theme

(defrule highlight :.highlight)
(defrule comments :.c :.cm :.cp :.c1 :.cs)
(defrule strings :.s :.s2 :.sx)
(defrule doc-string :.sd)
(defrule string-escape :.se)
(defrule string-symbol :.ss)
(defrule heredoc :sh)
(defrule error :.err)
(defrule keyword :.k)
(defrule keyword-declaration :.kd)
(defrule operator :.o)

(defstyles syntax
  (let [keyword-color (color/lighten text-color 5)
        comment-color (color/lighten text-color 10)
        string-color (hsl 19 54 45)
        string-symbol-color (hsl 83 31 30)]

    (highlight
     {:background :none}

     (keyword
      {:color keyword-color
       :font-weight :bold})

     (keyword-declaration
      {:color keyword-color
       :font-weight :bold})

     (comments
      {:color comment-color
       :font-style :italic})

     (strings
      {:color string-color})

     (string-symbol
      {:color string-symbol-color}))))

(defstyles theme
  [:body
   {:color text-color 
    :background-color background-color}]

  (a
   {:color anchor-color})

  (blockquote
   {:border-left [[(px 4) :solid text-color]]})

  (post-meta
   {:border-bottom [[(px 1) :dashed text-color]]})

  (post-content
   [:p
    (code
     {:color (color/lighten text-color 5)
      :background-color (color/darken background-color 5)})]))

(defstyles main 
  reset
  layout
  typography
  theme
  syntax)

