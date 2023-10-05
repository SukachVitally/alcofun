import ProductItem from "./ProductItem";
import "./ProductList.css";

const ProductList = (props) => {
  return (
    <div className="product-list">
      {props.products.map((item) => (
        <ProductItem key={item.id} product={item} />
      ))}
    </div>
  );
};

export default ProductList;
