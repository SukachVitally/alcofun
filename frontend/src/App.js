import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.min.js";
import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import ProductListPage from "./pages/ProductListPage";
import ProductPage from "./pages/ProductPage";
import RootLayout from "./pages/RootLayout";
import ErrorPage from "./pages/ErrorPage";
import ReactGA from 'react-ga4';

function App() {
  ReactGA.initialize(process.env.REACT_APP_TRACKING_ID);
  return (
    <RouterProvider
      router={createBrowserRouter([
        {
          path: "/",
          element: <RootLayout />,
          errorElement: <ErrorPage />,
          children: [
            { index: true, element:  <Navigate to={"/products/"} /> },
            {
              path: "/products/",
              element: <ProductListPage />,
            },
            { path: "/products/:id", element: <ProductPage /> },
          ],
        },
      ])}
    />
  );
}

export default App;
