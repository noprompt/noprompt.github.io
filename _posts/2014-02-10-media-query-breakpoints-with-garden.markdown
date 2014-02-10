---
layout: post
title:  "Media Query \"Breakpoints\" with Garden"
date:   2014-02-10
categories: clojurescript 
---


<img src="/img/deco_letter_o.png" class="lead-letter-image">
<span class="lead-letter">O</span>ver the weekend I spent a lot of
time hacking on the stylesheet for my blog which is, naturally,
written with [Garden][garden]. While working on the layout and
typography it struck me that I had completely forgotten about making
the blog responsive! Of course that meant I had to roll up my sleeves
and write some [media queries][media-queries].

Now, media queries aren't exactly the most amusing things in the world
to write by hand and can add a fair bit of complexity to a
stylesheet once there are several of them. Fortunately with Garden, the
full power of Clojure was at my disposal and I was able to write a
simple macro for creating CSS "breakpoints".

```clojure
(defmacro defbreakpoint [name media-params]
  `(defn ~name [& rules#]
     (at-media ~media-params
       [:& rules#])))
```

The idea here is to define a named media query *function* such that
all arguments passed to it become children of the media query. The
parent selector syntax (`:&`) is used so that the function is
applicable in *any* context, not just at the root level.

But what's the benefit? Why would I want to do this?

There are two motivations here. First, it gives me a higher level of
abstraction than doing something like the following.

```clojure
(def small-screen-params
  {:screen true
   :min-width (px 320)
   :max-width (px 480)})

(at-media small-screen-params
  [:.container
    {:width (px 480)}])
```

With `defbreakpoint` I can define new functions which encapsulate the
entire pattern in one location.

Second, it gives me new syntax for *communicating* my intentions. For
me, this is the primary benefit. Instead of littering my stylesheet
with comments explaining what each media query is used for or getting
marginal reuse from the code above, I have a function which clearly
expresses what is happening in the stylesheet.

```clojure
(defbreakpoint small-screen
  {:screen true
   :min-width (px 320)
   :max-width (px 480)})

(defbreakpoint medium-screen
  {:screen true
   :min-width (px 481)
   :max-width (px 1023})

(defbreakpoint large-screen
  {:screen true
   :min-width (px 1024)})
```

As you can see from the code above, the `defbreakpoint` syntax is
clean and certainly more attractive than handrolling `@media` queries
in pure CSS.

Let's see it in action!

```clojure
(css
 [:.container
  (small-screen
   [:& {:max-width (px 480)}])
  (medium-screen
   [:& {:max-width (px 760)}])
  (large-screen
  [:& {:max-width (px 1224)}])])
```

And the resulting CSS:

```css
@media screen and (min-width: 320px) and (max-width: 480px) {
  .container {
    max-width: 480px;
  }
}

@media screen and (min-width: 481px) {
  .container {
    max-width: 760px;
  }
}

@media screen and (min-width: 1024px) {
  .container {
    max-width: 1224px;
  }
}
```

In conclusion, I'm very happy to have been able to achieve this
amount expressivity and utility with such a short amount of code. This
was one of my first attempts at using Clojure's macro facilities with
Garden oriented code and I'm excited to see what else is possible.

[garden]: https://github.com/noprompt/garden
[media-queries]: https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Media_queries
