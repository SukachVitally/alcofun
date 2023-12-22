import { Outlet } from "react-router-dom";
import Navigation from "../components/Navigation/Navigation";
import Container from "react-bootstrap/Container";

const RootLayout = (props) => {
  return (
    <div className="App">
      <Navigation />
      <Container>
        <Outlet />
      </Container>
    </div>
  );
};

export default RootLayout;
