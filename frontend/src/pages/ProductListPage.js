import { useState, useEffect } from "react";
import Filter from "../components/ProductList/Filter";
import ProductList from "../components/ProductList/ProductList";
import Paginator from "../components/Paginator/Paginator";
import { useLocation, useNavigate } from "react-router-dom";
import queryString from "query-string";
import { Spinner } from "react-bootstrap";
import fetchJson from "../utils/http-utils.js";
import filterNullProperties from "../utils/object-utils";
import Sorting from "../components/ProductList/Sorting.js";
import "./ProductListPage.css";

function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [queryParams, setQueryParams] = useState({});
  const [sortingField, setSortingField] = useState(null);
  const [sortingOrder, setSortingOrder] = useState(null);

  const location = useLocation();
  const navigate = useNavigate();

  const filterApplyHandler = (filters) => {
    console.log("filters", filters);
    navigate(
      "/products/?" +
        queryString.stringify(
          filterNullProperties({
            ...queryParams,
            page: 1,
            categories: filters.categories,
            providers: filters.providers,
            characteristics: filters.characteristics,
            price_min: filters.price_min,
            price_max: filters.price_max,
          })
        )
    );
  };

  const sortingApplyHandler = (field, order) => {
    if (field != null) {
      navigate(
        "/products/?" +
          queryString.stringify(
            filterNullProperties({
              ...queryParams,
              sort: field != null ? field + "," + order : null,
            })
          )
      );
    }
  };

  async function fetchProducts(params) {
    setIsLoading(true);
    console.log(params);
    const json = await fetchJson(
      "product/?" + queryString.stringify(filterNullProperties(params))
    );
    setProducts(json.results);
    setPageCount(json.pageCount);
    setCurrentPage(json.currentPage);
    setIsLoading(false);
  }

  useEffect(() => {
    console.log("refresh page");
    const params = queryString.parse(location.search);

    if (params.sort !== undefined && params.sort != null) {
      const pair = params.sort.split(",");
      setSortingField(pair[0]);
      setSortingOrder(pair[1]);
    }

    setQueryParams(params);
    fetchProducts(params);
  }, [location]);

  return (
    <div className="Main">
      <div className="flex-justify-container">
        <Filter onFilterApply={filterApplyHandler} queryParams={queryParams} />
        <Sorting
          onChange={sortingApplyHandler}
          field={sortingField}
          order={sortingOrder}
        />
      </div>
      {isLoading && <Spinner />}
      {!isLoading && <ProductList products={products} />}
      {pageCount > 1 && (
        <Paginator
          currentPage={currentPage}
          pageCount={pageCount}
          params={queryParams}
        />
      )}
    </div>
  );
}

export default ProductListPage;
