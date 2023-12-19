# defacto todo

A basic example of using [defacto](https://github.com/skuttleman/defacto) with `reagent` and `datascript`.

- Install [Clojure runtime](https://clojure.org/guides/getting_started)
- Install [NodeJs](https://nodejs.org/en/download/package-manager/)

## Run It
```bash
$ npm i
$ clj -A:shadow -J-XX:-OmitStackTraceInFastThrow -m shadow.cljs.devtools.cli watch dev | grep --color=never -v DEBUG
```
