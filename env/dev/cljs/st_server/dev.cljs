(ns ^:figwheel-no-load st-server.dev
  (:require [st-server.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(core/init!)
