import Navigation from "../components/Navigation/Navigation";
import Footer from "../components/Footer/Footer";

const ErrorPage = () => {
  return <div className="App">
    <Navigation />
    <div className="flex-shrink-0">
      <h1>Something goes wrong!!!</h1>
    </div>
    <Footer />
  </div>;
};

export default ErrorPage;
