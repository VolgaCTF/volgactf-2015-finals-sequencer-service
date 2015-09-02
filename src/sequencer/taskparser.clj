(ns sequencer.taskparser
  (:require
    [clojure.string :as s]
    [sequencer.utils :refer :all]))

(def allowed-func
  ["count-nucleo" "dna-to-rna", "reverse-complement-dna", "hamming"])

(defn execute-task
  [task]
  (assuming [(contains? task :name)
             "Invalid task(no name field)",
             (contains? task :func)
             "Invalid task(no func field)",
             (contains? task :data)
             "Invalid task(no data field)",
             (contains? task :comment)
             "Invalid task(no comment field)",
             (in? allowed-func (get task :func))
             "Invalid function!"]
            (let [res-str (call (str "sequencer.taskparser/" (get task :func))
                                (transform-values-to-string (read-string (get task :data))))]
              (assoc task :result res-str))
            {:error why}))


(defn parse-strings
  [task-string]
  (->>
    (s/split-lines task-string)
    (map s/trim)
    (map #(s/split % #";"))
    (into {})
    (sequencer.utils/transform)))

(defn count-nucleo
  [{dna-str :s1}]
  (->>
    dna-str
    (into [])
    (frequencies)
    (seq)
    (map #(str (first %) ":" (second %)))
    (s/join " ")))

(defn dna-to-rna
  [{dna-str :s1}]
  (s/replace dna-str "T" "U"))

(defn reverse-complement-dna
  [{dna-str :s1}]
  (s/join "" (reverse
               (s/replace
                  dna-str #"A|T|G|C" {"A" "T" "T" "A" "G" "C" "C" "G"}))))

(defn hamming
  [{s1 :s1 s2 :s2}]
  (if (= (count s1) (count s2))
    (count (filter #(not= (get % 0) (get % 1)) (map vector s1 s2)))
    (throw (IllegalArgumentException. "Not equal strings is passed"))))