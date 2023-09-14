import { useState, useEffect } from "react";
import Filter from "../components/ProductList/Filter";
import ProductList from "../components/ProductList/ProductList";
import Paginator from "../components/Paginator/Paginator";
import { useLocation, useNavigate } from "react-router-dom";
import queryString from "query-string";
import { Spinner } from "react-bootstrap";
import fetchJson from "../utils/http-utils.js";

function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [queryParams, setQueryParams] = useState({});
  
  const location = useLocation();
  const navigate = useNavigate();

  const filterApplyHandler = (filters) => {
    console.log("filters", filters);
    navigate(
      "/products/?" +
        queryString.stringify({
          ...queryParams,
          page: 1,
          categories: filters.categories,
          providers: filters.providers,
          characteristics: filters.characteristics
        })
    );
  };

  async function fetchProducts(params) {
    setIsLoading(true);
    console.log(params);
    const json = await fetchJson("product/?" + queryString.stringify(params));
    setProducts(json.results);
    setPageCount(json.pageCount);
    setCurrentPage(json.currentPage);
    setIsLoading(false);
  }

  useEffect(() => {
    console.log("refresh page");
    const params = queryString.parse(location.search);
    setQueryParams(params);
    fetchProducts(params);
  }, [location]);

  return (
    <div className="Main">
      <Filter onFilterApply={filterApplyHandler} />
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
