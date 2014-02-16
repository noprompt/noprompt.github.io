---
layout: post
title:  "Autotesting ClojureScript"
date:   2014-01-25 6:00:00
categories: clojurescript testing ruby
---

<img src="/img/deco_letter_a.png" class="lead-letter-image">
<span class="lead-letter">A</span>t the time of this post, automatic
testing of ClojureScript code hasn't made it into
[`lein-cljsbuild`][lein-cljsbuild]. It is
[planned][lein-cljsbuild-gh-222], however, to appear in version
`2.0.0`. In the mean time we can get around this by using the
`:notify-command` option for test builds.

For this article I will assume you are using
[`clojurescript.test`][clojurescript.test] and your `project.clj`
contains roughly the equivalent code:

```clojure
(defproject my-project "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]]
  :plugins [[lein-cljsbuild "1.0.1"]
            [com.cemerick/clojurescript.test "0.2.1"]]
  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler
             {:output-to "target/testable.js"
              :optimizations :whitespace
              :pretty-print true}}]
   :test-commands {"unit-tests" ["phantomjs" :runner
                                 "window.literal_js_was_evaluated=true"
                                 "target/testable.js"]}})
```

To get auto testing we need to update our project to something
similar to the following:

```clojure
(defproject my-project "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]]
  :plugins [[lein-cljsbuild "1.0.1"]
            [com.cemerick/clojurescript.test "0.2.1"]]
  :profiles {:dev {:plugins [[com.cemerick/clojurescript.test "0.2.3-SNAPSHOT"]]}}
  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
			 :notify-command ["phantomjs" :cljs.test/runner "target/testable.js"]
             :compiler
             {:output-to "target/testable.js"
              :optimizations :whitespace
              :pretty-print true}}]})
```

All that we've done here is add
`[com.cemerick/clojurescript.test "0.2.3-SNAPSHOT"]` dependency to a
`:dev` profile and the `:notify-command` to our test build
configuration. Using the latest `SNAPSHOT` release of
`clojurescript.test` allows us to use the `:cljs.test/runner` keyword
in our `:notify-command` configuration.

Now we can run `lein cljsbuild auto test` from the
command line and watch as our tests get run immediately after compile. 

```
Compiling ClojureScript.
Compiling "target/testable.js" from ["src" "test"]...
Successfully compiled "target/testable.js" in 7.33223 seconds.

Testing demo.core-test

Ran 10 tests containing 36 assertions.
0 failures, 0 errors.
{:test 10, :pass 36, :fail 0, :error 0, :type :summary}
```

To top it off you can add an `auto-test` alias to your `project.clj`.

```clojure
:aliases {"auto-test" ["do" "clean," "cljsbuild" "auto" "test"]}
```

Happy auto testing!

[lein-cljsbuild]: https://github.com/emezeske/lein-cljsbuild
[lein-cljsbuild-gh-222]: https://github.com/emezeske/lein-cljsbuild/pull/222
[clojurescript.test]: https://github.com/cemerick/clojurescript.test
