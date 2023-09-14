import Card from "react-bootstrap/Card";
import { NavLink } from "react-router-dom";
import "./ProductItem.css";

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
          title={product.name}
          data-toggle="tooltip"
          data-placement="top"
        >
          {product.name}
        </Card.Title>
        <Card.Text>
          The best price is <b>{formattedPrice}</b>
        </Card.Text>
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
