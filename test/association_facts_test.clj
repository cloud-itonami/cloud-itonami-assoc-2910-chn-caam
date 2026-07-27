(ns association-facts-test
  (:require [clojure.java.io :as io] [clojure.java.shell :as shell]
            [clojure.test :refer [deftest is testing]]
            [kotoba.compiler.core :as compiler] [kotoba.compiler.ir :as ir]))
(def source (slurp "src/association_facts.kotoba"))
(defn call [kir function & args] (ir/execute kir function (vec args)))
(defn present [option] (when (second option) (nth option 2)))
(def fields ["id" "title" "association" "isic" "country" "kind" "url"
             "url-provenance" "established-date" "last-revised-date" "retrieved-at"])
(def expected
  [{"id" "caam.about-organization"
    "title" "中国汽车工业协会简介 (About CAAM, organization profile)"
    "association" "caam" "isic" "2910" "country" "CHN" "kind" "governance-program"
    "url" "http://www.caam.org.cn/chn/2/cate_7/con_5223237.html"
    "url-provenance" "official-association-site" "established-date" "1990-01-01"
    "last-revised-date" "2023-06-12" "retrieved-at" "2026-07-27"}
   {"id" "caam.industry-self-discipline-scope"
    "title" "行业自律 (industry self-discipline), one of CAAM's twelve stated 业务范围"
    "association" "caam" "isic" "2910" "country" "CHN" "kind" "self-regulatory-mandate"
    "url" "http://www.caam.org.cn/chn/2/cate_7/con_5223237.html"
    "url-provenance" "official-association-site" "established-date" nil
    "last-revised-date" "2023-06-12" "retrieved-at" "2026-07-27"}])

(deftest reference-preserves-present-fields-absence-and-topics
  (let [kir (:kir (compiler/compile-source source :js-kotoba-v1))
        observed (mapv (fn [i] (into {} (map (fn [f] [f (present (call kir 'entry-field "caam" i f))]) fields))) [0 1])]
    (is (= expected observed))
    (testing "the self-discipline mandate has no separate establishment date, and it is NOT inherited from entry 0"
      (is (= ["1990-01-01" nil] (mapv #(present (call kir 'entry-field "caam" % "established-date")) [0 1]))))
    (is (= [2 1] (mapv #(call kir 'topic-count "caam" %) [0 1])))
    (is (= ["governance" "standards"] (mapv #(present (call kir 'topic "caam" 0 %)) [0 1])))
    (is (= "caam.industry-self-discipline-scope" (present (call kir 'by-topic-id "caam" "self-discipline" 0))))
    (is (= #{} (set (:effects kir))))
    (testing "unknown values and invalid indexes fail closed"
      (is (zero? (call kir 'entry-count "cba")) "an association this repo does not cover")
      (is (zero? (call kir 'entry-count "vda")) "a sibling association is not covered here")
      (is (nil? (present (call kir 'entry-field "caam" -1 "id"))))
      (is (nil? (present (call kir 'entry-field "caam" 2 "id"))))
      (is (nil? (present (call kir 'entry-field "caam" 0 "member-count"))))
      (is (nil? (present (call kir 'topic "caam" 1 1))))
      (is (zero? (call kir 'by-topic-count "caam" "human-rights")))
      (is (nil? (present (call kir 'by-topic-id "caam" "self-discipline" 1)))))
    (testing "the coverage note records that only one source page was reachable"
      (is (re-find #"reachable from the authoring network"
                   (present (call kir 'coverage-note "caam")))))))

(defn compiler-root []
  (nth (iterate #(.getParent ^java.nio.file.Path %)
                (java.nio.file.Path/of (.toURI (io/resource "kotoba/compiler/core.clj")))) 4))
(defn base64 [value] (.encodeToString (java.util.Base64/getEncoder) value))
(deftest restricted-javascript-and-typed-wasm-conform-semantically
  (let [javascript (compiler/compile-source source :js-kotoba-v1)
        wasm (compiler/compile-source source :wasm32-browser-kotoba-v1)
        js64 (base64 (.getBytes ^String (:source javascript) "UTF-8")) wasm64 (base64 ^bytes (:bytes wasm))
        probe (shell/sh "node" "--input-type=module" "-e"
                (str "import(process.argv[1]).then(async host=>{const j=await import('data:text/javascript;base64," js64 "');"
                     "const w=await host.instantiateKotoba(Buffer.from(process.argv[2],'base64'));const run=x=>{"
                     "if(x['entry-count']('caam')!==2n||x['entry-field']('caam',1n,'established-date')[1]!==false||x['entry-field']('caam',0n,'established-date')[2]!=='1990-01-01')throw Error('dates');"
                     "if(x['topic-count']('caam',0n)!==2n||x['topic']('caam',0n,1n)[2]!=='standards'||x['topic-count']('caam',1n)!==1n)throw Error('topics');"
                     "if(x['by-topic-id']('caam','self-discipline',0n)[2]!=='caam.industry-self-discipline-scope'||x['topic']('caam',1n,1n)[1]!==false)throw Error('query');};"
                     "run(j.instantiateKotoba({}));run(w.instance.exports);}).catch(e=>{console.error(e);process.exit(99)})")
                (.toString (.toUri (.resolve (compiler-root) "runtime/browser-host.mjs"))) wasm64)]
    (is (zero? (:exit probe)) (str (:out probe) (:err probe)))))
(deftest production-source-authority
  (is (= ["src/association_facts.kotoba"]
         (->> (file-seq (io/file "src")) (filter #(.isFile %)) (map str) sort vec))))
