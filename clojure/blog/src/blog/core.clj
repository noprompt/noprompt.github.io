(ns blog.core
  (:refer-clojure :exclude [+ - * / comment keyword])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [garden.def :refer [defrule defcssfn defstyles defstylesheet]]
            [garden.units :refer [px em percent] :rename {percent %}]
            [garden.color :as color :refer [hsl]]
            [garden.stylesheet :refer [at-import]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.repl]
            [blog.util :as util :refer [modular-scale-fn]]))

;; Utilities

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rules

;; HTML Tags

(defrule a :a)
(defrule on-hover :&:hover)
(defrule visited? :&:visited)
(defrule pre :pre)
(defrule code :code)
(defrule blockquote :blockquote)

;; Blog

(defrule site :.site)
(defrule site-title :.site-title)
(defrule content :.content)
(defrule post :.post)
(defrule post-meta :.post-meta)
(defrule post-date :.post-date)
(defrule post-title :.post-title)
(defrule post-content :.post-content)
(defrule footer :.footer)
(defrule contact :.contact)

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
  (blockquote
   {:padding-left (ms 0)})

  (site
   {:height (% 100)
    :overflow :scroll})

  (content
   util/container
   {:margin [[0 "auto"]]})

  (footer
   util/container
   {:margin [[0 "auto"]]})

  (post-title
   {:padding [[(ms 2) 0]]})

  (post-content
   [:p
    {:margin [[(ms 1) 0]]}]

   (pre
    {:overflow-x :scroll}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Small screens

  (util/small-screen
   (post
    (util/offset 1)
    (util/col 10)))

  ;; Medium screens

  (util/medium-screen
   (post
    (util/offset 1)
    (util/col 10)))

  ;; Large screens

  (util/large-screen
   (post
    (util/offset 2)
    (util/col 8))
   
   (footer
    (contact
     (util/offset 2)
     (util/col 8))))

  ;; Very large screens

  (util/x-large-screen
   (post
    (util/offset 3)
    (util/col 6))))

;;;; Typography

(defstyles typography
  [:body
   {:font-family copy-font}]

  (blockquote
   {:font-family copy-font
    :font-style :italic})

  (code
   {:font-family code-font})

  (site-title
   {:text-transform :uppercase}
   (a
    {:color :inherit}))

  (post-date
   {:font {:family copy-font
           :style :italic}
    :text-align :center})

  (post-title
   {:text-align :center
    :font {:family heading-font 
           :weight 100}})

  (post-content
   {:font-family copy-font}

   [:p
    {:text-align :justify}])

  ;; Small screens

  (util/small-screen
   (post-title
    {:font-size (ms 5)})

   (post-date
    {:font-size (ms 1)})

   (post-content
    {:font-size (ms 1)
     :line-height (ms 3)}
    (code
     {:font-size (ms 0)}))

   (pre
    {:font-size (ms 0)
     :line-height (ms 2)}))

  ;; Medium screens

  (util/medium-screen
   (post-title
    {:font-size (ms 5)})

   (post-date
    {:font-size (ms 1)})

   (post-content
    {:font-size (ms 1)
     :line-height (ms 3)}
    (code
     {:font-size (ms 0)}))

   (pre
    {:font-size (ms 0)
     :line-height (ms 2)}))

  ;; Large screens

  (util/large-screen
   (post-title
    {:font-size (ms 7)})

   (post-date
    {:font-size (ms 2)})

   (post-content
    {:font-size (ms 2)
     :line-height (ms 4)}
    (code
     {:font-size (ms 1)}))

   (pre
    {:font-size (ms 0)
     :line-height (ms 2)}))

  ;; iPhone 5

  (util/iphone-5
   (post-title
    {:font-size (ms 4)})

   (post-date
    {:font-size (ms 0)})

   (post-content
    {:font-size (ms -1)
     :line-height (ms 1)}
    (code
     {:font-size (ms -1.5)}))

   (pre
    {:font-size (ms -1)
     :line-height (ms 1)})))

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
     {:padding (ms 0.5)
      :background-color (color/darken background-color 3)}

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
  (site
   {:color text-color 
    :background-color background-color}
   ^:prefix
   {:box-shadow [[:inset 0 0 (ms 8) (color/darken background-color 15)]
                 [:inset 0 0 (ms 10) (color/darken background-color 10)]]})

  (a
   {:color anchor-color}
   (on-hover
    {:color (color/darken anchor-color 10)}))

  (blockquote
   {:border-left [[(px 4) :solid text-color]]})

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

