# defacto todo

An example of using [defacto](https://github.com/skuttleman/defacto) with `reagent` and `datascript`.

## Why `defacto`?

Notice the power from the [stratified design](https://dspace.mit.edu/bitstream/handle/1721.1/6064/AIM-986.pdf?sequence=2&isAllowed=y)
and `defacto`'s [plugin architecture](https://medium.com/hackernoon/why-the-open-closed-principle-is-the-one-you-need-to-know-but-dont-176f7e4416d) at play here.
Each module of [defacto](https://github.com/skuttleman/defacto) adds a semantic layer which uses the "primitives"
defined by the stratum beneath it.

- `defacto-core` - provides extremely basic primitives for managing state using the (well, if you squinch your eyes)
[CQS](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation) pattern.
- `defacto-res` and `defacto-forms` - provide low-level functionality for managing *async behavior* and *arbitrary user input* respectively.
- `defacto-forms+` - *combines the components of the lower layer* ^^ to connect user-input with an asynchronous resource.
- the `defacto-todo.store` namespace utilizes `defacto-forms+` to expose domain-specific functionality to the todo app.

These composable patterns unlock enormous flexibility that can allow you to decide **_exactly_** how much state store you need,
and how ["DS" your "L"](https://en.wikipedia.org/wiki/Domain-specific_language) should be.

## Dependencies

- Install [Clojure runtime](https://clojure.org/guides/getting_started)
- Install [NodeJs](https://nodejs.org/en/download/package-manager/)

## Run It
```bash
$ git clone https://github.com/skuttleman/defacto-todo.git
$ npm i
$ clj -A:shadow -J-XX:-OmitStackTraceInFastThrow -m shadow.cljs.devtools.cli watch dev
```
