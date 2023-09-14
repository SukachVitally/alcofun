import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import queryString from "query-string";

function Navigation() {
  const location = useLocation();
  const navigate = useNavigate();
  const [search, setSearch] = useState("");

  const searchChangeHandler = (event) => {
    setSearch(event.target.value);
  };

  const submitHandler = (event) => {
    event.preventDefault();
    if (search === "") {
      navigate("/products/");
    } else {
      navigate("/products/?search=" + search);
    }
  };

  useEffect(() => {
    const params = queryString.parse(location.search);
    setSearch(params.search || "");
  }, [location]);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <NavLink className="navbar-brand" to="/">
          AlcoFun
        </NavLink>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <NavLink to="/" className="nav-link">
              Home
            </NavLink>

            <NavLink to="/products/" className="nav-link">
              Products
            </NavLink>
          </Nav>
          <Form onSubmit={submitHandler}>
            <Row>
              <Col xs="auto">
                <Form.Control
                  type="text"
                  placeholder="Search"
                  className=" mr-sm-2"
                  value={search}
                  onChange={searchChangeHandler}
                />
              </Col>
              <Col xs="auto">
                <Button type="submit">Submit</Button>
              </Col>
            </Row>
          </Form>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default Navigation;
