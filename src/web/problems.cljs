(ns web.problems)

(def min-x 0)
(def max-x 99)

; todo distributions are uneven
(def generators
  {'+ #(let [a (rand-int (inc max-x))
             b (rand-int (- max-x a))]
         {:a a :b b :op '+ :solution (+ a b)})
   '- #(let [a (rand-int (inc max-x))
             b (rand-int a)]
         {:a a :b b :op '- :solution (- a b)})
   '* #(let [a (rand-int (inc max-x))
             b (rand-int (if (zero? a)
                           max-x
                           (/ max-x a)))]
         {:a a :b b :op '* :solution (* a b)})
   ;'/ #(let [a (rand-int (inc max-x))
   ;          b (rand-int (/ max-x a))]
   ;      {:a a :b b :op '* :solution (/ a b)})
   })

(defn generate-problem!
  []
  (let [generate! (-> (keys generators) rand-nth generators)]
    (-> (generate!)
        (assoc :id (random-uuid)))))
