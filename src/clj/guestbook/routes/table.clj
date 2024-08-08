(ns guestbook.routes.table
  (:require 
   [guestbook.middleware :as middleware]
   [guestbook.layout :as layout]
   [ring.util.http-response :as response]
   [struct.core :as st]
   [schema.core :as s]))

(def table-data (atom [{:sku "1"
                        :name "горілка"
                        :planogram 10
                        :stock 10
                        :shelf 10
                        :replenish 0
                        :put 10
                        :empty "ні"
                        :color "tr-norm"}
                       {:sku "2"
                        :name "печево"
                        :planogram 10
                        :stock 10
                        :shelf 5
                        :replenish 5
                        :put 5
                        :empty "ні"
                        :color "tr-yellow"}
                       {:sku "3"
                        :name "сік"
                        :planogram 10
                        :stock 0
                        :shelf 0
                        :replenish 10
                        :put 0
                        :empty "так"
                        :color "tr-red"}]))

(defn get-sku [id]
  (first (filter #(= (:sku %) id) @table-data)))

(defn table-page
  [request]
  (layout/render request "table.html" {:data @table-data}))

(defn sku-page
  [request] 

  (let 
   [{{:keys [id]} :path-params} 
    request
    
    sku 
    (get-sku id)]

    (layout/render request "sku.html" sku)))

(def sku-schema
  [[:put st/required st/string
    {:message "не менше 1"
     :validate (fn [x] (pos-int? (Integer/parseInt x)))}]])

(defn validate-sku
  [params]
  (first
   (st/validate params sku-schema)))

(defn update-sku [id val]
  (prn "Update " id "value " val))

(defn save-sku!
  [request]
  (let [{:keys [params]}
        request

        errors
        (validate-sku params)

        _ (prn errors)
        
        {{:keys [id]} :path-params}
        request
        
        val 
        (:put params)
        
        sku
        (get-sku id)]
    
    (if-let [errors (validate-sku params)]
      (layout/render request "get-sku.html" (assoc sku :errors errors))
      (do 
        (update-sku id val)
        (layout/render request "get-sku.html" sku)))))

(defn get-sku-view 
  [request]
  (let
   [{{:keys [id]} :path-params}
    request
  
    sku
    (get-sku id)]
  
    (layout/render request "get-sku.html" sku)))

(defn table-routes []
  [["/" {:get table-page}]
   ["/sku/:id" {:put save-sku! 
                :get sku-page}]
   ["/get/sku/:id" {:get get-sku-view}]])
