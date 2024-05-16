(ns web.index
  (:require
    [numbers.deutsch :as n]
    [web.problems :as p]
    [reagent.core :as r]
    [reagent.dom :as r.dom]))

(defonce state
  (r/atom {:streaks            {:best   0
                                :active 0}
           :problem            (p/generate-problem!)
           :input              nil
           :answer-is-visible? false}))

(defn header
  []
  [:div.header
   [:div.streak
    [:span.label "Active Streak: "]
    [:span.value (get-in @state [:streaks :active])]]
   [:div.streak
    [:span.label "Best Streak: "]
    [:span.value (get-in @state [:streaks :best])]]])

(defn mode
  []
  ; rush in x min
  ; do x problems
  ; unlimited
  [:div "="])

(defn next-problem
  [state-val]
  (assoc state-val
    :problem (p/generate-problem!)
    :input nil
    :answer-is-visible? false))

(defn submit!
  []
  (let [{{:keys [solution]} :problem
         :keys              [input]} @state]
    (if (= solution input)
      (swap! state (fn [state-val]
                     (-> state-val
                         (update-in [:streaks :active] inc)
                         (update-in [:streaks :best] max (inc (get-in state-val [:streaks :active])))
                         (next-problem))))
      (swap! state (fn [state-val]
                     (-> state-val
                         (assoc-in [:streaks :active] 0)
                         (assoc :answer-is-visible? true)))))))

(defn input
  []
  ; todo force remount on problem change
  [:form#solution
   {:on-submit (fn [e]
                 (.stopPropagation e)
                 (.preventDefault e)
                 (submit!))}
   [:input {:type      "number"
            :step      1 :min p/min-x :max p/max-x
            :value     (:input @state)
            ; todo NaN
            :on-change #(swap! state assoc :input (some-> % .-target .-value js/parseInt))
            :autoFocus true}]])

(defn problem
  []
  (let [{{:keys [a b op solution id]} :problem
         :keys
         [answer-is-visible? streaks]} @state]
    ^{:key id}
    [:div.problem
     {:class (cond
               ;(= 0 (:active streaks)) "pulse-red"
               ;(not= 0 (:active streaks)) "pulse-green"

               )}
     [:p.a (n/numbers a)]
     [:p.op (str op) #_(n/ops op)]
     [:p.b (n/numbers b)]
     [:p.eq "= "]
     (if answer-is-visible?
       [:div
        [:p.algebraic a " " op " " b " = " solution]
        [:form#next
         {:on-submit (fn [e]
                       (.stopPropagation e)
                       (.preventDefault e)
                       (swap! state next-problem))}
         [:button {:type "submit" :autoFocus true} "Next Problem"]]]
       [input])]))

(defn app []
  [:div.app
   [header]
   [problem]])

(defn start
  {:dev/after-load true}
  []
  (r.dom/render [app] (js/document.getElementById "app")))

(defn ^:export init []
  (start))
