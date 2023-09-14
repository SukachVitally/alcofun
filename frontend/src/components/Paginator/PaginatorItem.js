import { Link } from "react-router-dom";

const PaginatorItem = (props) => {
  return (
    <li className="page-item">
      <Link
        to={`/products/?${props.params}`}
        className={`page-link ${props.active ? "active" : ""}`}
        role="button"
      >
        {props.page}
      </Link>
    </li>
  );
};

export default PaginatorItem;
