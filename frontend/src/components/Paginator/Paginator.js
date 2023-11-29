import Pagination from "react-bootstrap/Pagination";
import PaginatorItem from "./PaginatorItem";
import PaginatorItemNext from "./PaginatorItemNext";
import PaginatorItemPrevious from "./PaginatorItemPrevious";
import queryString from "query-string";

const Paginator = (props) => {
  const { currentPage, pageCount, params } = props;

  const createPaginatorItem = (page) => (
    <PaginatorItem
      params={queryString.stringify({ ...params, page: page })}
      page={page}
      active={page === currentPage}
    />
  );

  return (
    <Pagination className="d-flex justify-content-center">
      {currentPage !== 1 && (
        <PaginatorItemPrevious
          params={queryString.stringify({ ...params, page: currentPage - 1 })}
          page={currentPage - 1}
        />
      )}
      {currentPage !== 1 && createPaginatorItem(1)}

      {currentPage > 3 && <Pagination.Ellipsis />}

      {currentPage > 2 && createPaginatorItem(currentPage - 1)}
      {createPaginatorItem(currentPage)}
      {currentPage < pageCount - 1 && createPaginatorItem(currentPage + 1)}

      {currentPage < pageCount - 2 && <Pagination.Ellipsis />}

      {currentPage !== pageCount && createPaginatorItem(pageCount)}
      {currentPage !== pageCount && (
        <PaginatorItemNext
          params={queryString.stringify({ ...params, page: currentPage + 1 })}
          page={currentPage + 1}
        />
      )}
    </Pagination>
  );
};

export default Paginator;
