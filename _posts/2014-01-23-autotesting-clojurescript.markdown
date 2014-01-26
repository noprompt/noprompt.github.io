---
layout: post
title:  "Autotesting ClojureScript"
date:   2014-01-25 6:00:00
categories: clojurescript testing ruby
---

At the time of this post, automatic testing of ClojureScript code
hasn't made it into [`lein-cljsbuild`][lein-cljsbuild]. It is
[planned][lein-cljsbuild-gh-222], however, to appear in version
`2.0.0`. In the mean time we can get around this by using the Ruby
[`watchr`][watchr] gem.

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

Because we will be using our own testing solution instead of `lein
cljsbuild test`, it will be necessary to save a copy of the
[test runner code][runner.js] &mdash; which now comes prepackaged with
`clojurescript.test` &mdash; to somewhere in your
project directory. I like to save this file to `test/runner.js`.

Next, install the `watchr` gem from the command line with `gem install
watchr` and create a `watchr` file in the root of your project
directory with the following contents:

```ruby
watch("target/testable.js") do |file|
  system("phantomjs test/runner.js window.literal_js_was_evaluated=true target/testable.js")
end
```

This instructs `watchr` to execute the contents of the `do` block
whenever it detects changes to the `target/testable.js` file. As you can tell
we are executing the same `phantomjs` command as `lein cljsbuild test`
with the exception the `:runner` keyword has been manually replaced
with the location of the `runner.js` file we created earlier.

At this point we can open two seperate terminal windows, change to our
project directory and run `lein cljsbuild auto test` in one and
`watchr ./watchr` in the other. Now, whenever we modify and save code
in our `:source-paths`, `target/testable.js` will be recompiled and `watchr`
will run our `phantomjs` command. If everything goes right we should
see the results of our tests in the window where `watchr` is running.

```
Compiling ClojureScript.
Compiling "target/testable.js" from ["src" "test"]...
Successfully compiled "target/testable.js" in 7.33223 seconds.

Testing demo.core-test

Ran 10 tests containing 36 assertions.
0 failures, 0 errors.
{:test 10, :pass 36, :fail 0, :error 0, :type :summary}
```

This is great but, personally, I'm not a big fan of having two
terminal windows open. Here's a little Ruby script I use to start up
both processes:

```ruby
#!/usr/bin/env ruby

cljsbuild_pid = fork { system("lein cljsbuild auto test") }
watchr_pid = fork { system("watchr ./watchr") }

begin
  while true do
    sleep 1000
  end
rescue Interrupt
ensure
  Process.kill(:INT, cljsbuild_pid) 
  Process.kill(:INT, watchr_pid) 
end
```

Happy testing!

[lein-cljsbuild]: https://github.com/emezeske/lein-cljsbuild
[lein-cljsbuild-gh-222]: https://github.com/emezeske/lein-cljsbuild/pull/222
[watchr]: https://github.com/mynyml/watchr 
[bundler]: http://bundler.io/
[clojurescript.test]: https://github.com/cemerick/clojurescript.test
[runner.js]: https://raw.github.com/cemerick/clojurescript.test/master/resources/cemerick/cljs/test/runner.js
