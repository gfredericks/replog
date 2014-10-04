# replog

A Clojure library for keeping repl history and making it lexically
available via names such as `&1`, `&2`, etc.

Currently just a proof of concept.

## Usage

`lein install`

Add `[com.gfredericks/replog "0.1.0-SNAPSHOT"]` to a profile in some
project, and add `#'com.gfredericks.replog/wrap-history` to your list
of nrepl middlewares.

Then `lein repl`:

``` clojure
user=> (* 2 3 7)
&2:
42
user=> (inc &2)
&3:
43
```

## Eventual Features

- a leiningen plugin
- naming configurability (not just `&42`)
- garbarge collection (currently it's just a big memory leak)
- tracking inputs as well
- exporting?

## License

Copyright © 2014 Gary Fredericks

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
