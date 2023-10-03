import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import { useState } from "react";

function Sorting(props) {
  const [field, setField] = useState(props.field != null ? props.field : "");
  const [order, setOrder] = useState(props.order != null ? props.order : "");

  const fieldChangeHandler = (event) => {
    setField(event.target.value)
    props.onChange(event.target.value, order);
  }

  const orderChangeHandler = (event) => {
    setOrder(event.target.value);
    props.onChange(field, event.target.value);
  }

  return (
    <>
      <p>
        <button
          className="btn btn-primary"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#collapse-sorting"
        >
          Sotring
        </button>
      </p>
      <div className="collapse" id="collapse-sorting">
        <Form.Group as={Row} className="mb-3" controlId="sorting-field">
          <Form.Label column sm="2">
            Field
          </Form.Label>
          <Col sm="10">
            <Form.Select aria-label="Field" value={field} onChange={fieldChangeHandler} >
              <option value="">---</option>
              <option value="name">Name</option>
              <option value="created_at">Created at</option>
              <option value="rating">Rating</option>
            </Form.Select>
          </Col>
        </Form.Group>
        <Form.Group as={Row} className="mb-3" controlId="sorting-order">
          <Form.Label column sm="2">
            Order
          </Form.Label>
          <Col sm="10">
            <Form.Select aria-label="Order" value={order} onChange={orderChangeHandler}>
              <option value="asc">ASC</option>
              <option value="desc">DESC</option>
            </Form.Select>
          </Col>
        </Form.Group>
      </div>
    </>
  );
}

export default Sorting;
