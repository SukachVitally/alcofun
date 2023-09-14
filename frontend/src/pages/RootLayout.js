import { Outlet } from "react-router-dom";
import Navigation from "../components/Navigation/Navigation";
import Footer from "../components/Footer/Footer";
import Container from "react-bootstrap/Container";

const RootLayout = (props) => {
  return (
    <div className="App">
      <Navigation />
      <Container>
        <Outlet />
      </Container>
      <Footer />
    </div>
  );
};

export default RootLayout;
