import { useTranslation } from "react-i18next";
import Accordion from 'react-bootstrap/Accordion';
import Graph from "./Graph";

const ProductHistory = (props) => {
    const { t } = useTranslation();

    const clickHandler = () => {
        console.log("click")
    }

    return (
        <Accordion defaultActiveKey="0">
        <Accordion.Item eventKey="0">
          <Accordion.Header>Accordion Item #1</Accordion.Header>
          <Accordion.Body>
            <Graph />
          </Accordion.Body>
        </Accordion.Item>
      </Accordion>
    );

}

export default ProductHistory;