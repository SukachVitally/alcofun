import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { NavLink } from "react-router-dom";

const CharacteristicItem = (props) => {
  return (
    <Row className="border-bottom">
      <Col>{props.name}</Col>
      <Col xs={6}>
        <NavLink to={`/products/?characteristics=${props.id}`}>
          {props.value}
        </NavLink>
      </Col>
    </Row>
  );
};

export default CharacteristicItem;
