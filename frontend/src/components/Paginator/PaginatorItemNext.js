import { Link } from "react-router-dom";

const PaginatorItemNext = (props) => {
  return (
    <li className="page-item">
      <Link
        to={`${props.path}?${props.params}`}
        className={"page-link"}
        role="button"
      >
        <span aria-hidden="true">›</span>
        <span className="visually-hidden">Next</span>
      </Link>
    </li>
  );
};

export default PaginatorItemNext;
