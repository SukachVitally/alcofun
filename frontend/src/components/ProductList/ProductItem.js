import Card from "react-bootstrap/Card";
import { NavLink } from "react-router-dom";
import "./ProductItem.css";
import StarRatings from "react-star-ratings";

function ProductItem(props) {
  const product = props.product;

  const formattedPrice = product.price.toLocaleString("en-US", {
    style: "currency",
    currency: "UAH",
    minimumFractionDigits: 2,
  });

  return (
    <Card className="d-inline-block product-item">
      <Card.Img
        variant="top"
        src={product.imageUrl}
        className="product-item__image"
        title={product.name}
        data-toggle="tooltip"
        data-placement="top"
      />
      <Card.Body>
        <Card.Title
          style={{ color: product.hot ? "red" : "black" }}
          title={product.name}
          data-toggle="tooltip"
          data-placement="top"
        >
          {product.name}
        </Card.Title>
        <Card.Subtitle>
          <StarRatings
            rating={product.rating != null ? product.rating.rating : 0}
            starRatedColor="#ebd834"
            numberOfStars={5}
            starDimension="25"
            name="rating"
          />
        </Card.Subtitle>
        <Card.Body>
          The best price is <b>{formattedPrice}</b>
        </Card.Body>
        <div className="product-item__footer">
          <NavLink to={`/products/${product.id}`} className="btn btn-primary">
            View
          </NavLink>
        </div>
      </Card.Body>
    </Card>
  );
}

export default ProductItem;
