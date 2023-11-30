import { Link } from "react-router-dom";

const PaginatorItemPrevious = (props) => {
  return (
    <li className="page-item">
      <Link
        to={`${props.path}?${props.params}`}
        className={"page-link"}
        role="button"
      >
        <span aria-hidden="true">‹</span>
        <span className="visually-hidden">Previous</span>
      </Link>
    </li>
  );
};

export default PaginatorItemPrevious;