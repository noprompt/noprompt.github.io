(ns blog.core
  (:refer-clojure :exclude [+ - * / comment keyword])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [garden.def :refer [defrule defcssfn defstyles defstylesheet]]
            [garden.units :refer [px em percent] :rename {percent %}]
            [garden.color :as color :refer [hsl]]
            [garden.stylesheet :refer [at-import rule cssfn]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.repl]
            [blog.util :as util :refer [modular-scale-fn]]))

(defcssfn url)

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
  (fonts "Della Respira" "Gentium Basic" "serif"))

(def code-font
  (fonts "Consolas" "Ubuntu Mono" "Courier" "monospace"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rules

;; HTML Tags

(defrule a :a)
(defrule on-hover :&:hover)
(defrule visited? :&:visited)
(defrule pre :pre)
(defrule code :code)
(defrule blockquote :blockquote)
(defrule ul :ul)
(defrule ol :ol)
(defrule xl :ul :ol)
(defrule li :li)

;; Blog

(defrule home-page :#home)
(defrule post-page :#post)

(defrule title :.title)
(defrule date :.date)

(defrule site :.site)
(defrule site-title :.site-title)
(defrule content :.content)
(defrule post :.post)
(defrule post-meta :.post-meta)
(defrule post-date :.post-date)
(defrule post-title :.post-title)
(defrule post-content :.post-content)
(defrule header :.header)
(defrule header-inner :.header-inner)
(defrule footer :.footer)
(defrule footer-inner :.footer-inner)
(defrule contact :.contact)

(defrule before :&:before)
(defrule after :&:after)

;;;; Reset

(defstyles reset
  [:* {:margin 0 :padding 0}]

  [:*, :*:before, :*:after
   ^:prefix
   {:box-sizing "border-box"}]

  [:html, :body
   {:height (% 100)}]

  [:ul
   {:list-style :none}]
  
  (a {:text-decoration "none"}))

;;;; Layout

(defstyles layout
  (blockquote
   {:padding-left (ms 0)})

  ;; Home page

  (home-page
   util/container
   {:margin [[0 "auto"]]
    :padding-top (ms 5)})

  (post-page
   util/container
   {:margin [[0 "auto"]]
    :padding-top (ms 2)})

  ;; Post page

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
   (let [block (util/block :offset 1 :col 10)]
     (post block)))

  ;; Medium screens

  (util/medium-screen
   (let [block (util/block :offset 1 :col 10)]
     (post block)))

  ;; Large screens

  (util/large-screen
   (let [block (util/block :offset 2 :col 8)]
     (post block)))

  ;; Very large screens

  (util/x-large-screen
   (let [block (util/block :offset 3 :col 6)]
     (post block))))

;;;; Typography

(defstyles typography
  [:body
   {:font-family copy-font}]

  (home-page
   (site-title
    {:font-size (ms 7)
     :line-height (ms 7)
     :text-align :center}))

  (post-page
   (site-title
    {:text-align :center}))

  (blockquote
   {:font-family copy-font
    :font-style :italic})

  (code
   {:font-family code-font})

  (site-title
   {:font {:size (ms 2)
           :family heading-font}
    :text {:transform :uppercase}
    :line-height (ms 4)}

   (a
    {:color :inherit}))

  (post-date
   {:font {:family copy-font}
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
  [:body
   {:color text-color 
    :background-color background-color}]

  (post-page
   (site-title
    {:color (color/lighten text-color 30)}
    (on-hover
     {:color text-color})))
 
  (a
   {:color anchor-color}
   (on-hover
    {:color (color/darken anchor-color 10)}))

  (blockquote
   {:border-left [[(px 4) :solid text-color]]})

  (post-date
   {:color (color/lighten text-color 20)})

  (post-content
   [:p
    (code
     {:color (color/lighten text-color 5)
      :background-color (color/darken background-color 5)})]))

(defstyles table-of-contents
  (let [toc (rule :.toc)
        title (rule :.toc-title)
        entries (rule :.toc-entries)
        entry (rule :.toc-entry)
        entry-key (rule :.toc-entry-key)
        entry-val (rule :.toc-entry-val)]
    (toc
     (util/block :offset 2 :col 8) 

     (title
      {:padding (ms 5)}
      {:font {:size (ms 3)
              :weight :normal}
       :text-align :center})

     (entries
      {:width (% 100)
       :padding 0
       :overflow-x :hidden}

      (entry
       (before
        {:float :left
         :width 0
         :white-space :nowrap
         :content [(for [i (range 5)]
                     "'. . . . . . . . . . . . . . . . . . . . '")]}
        {:font-family heading-font
         :line-height (ms 2.2)}
        {:color (color/lighten text-color 30)})

       (entry-key
        {:padding-right (ms -1)}
        {:font {:size (ms 2)
                :family heading-font}
         :line-height (ms 1)}
        {:background background-color})

       (entry-val
        {:float :right
         :padding-left (ms 0)}
        {:font-size (ms 0.5)
         :line-height (ms 2)}
        {:color (color/lighten text-color 10)
         :background background-color}))))))

(defstyles main 
  reset
  layout
  typography
  theme
  syntax
  table-of-contents)

