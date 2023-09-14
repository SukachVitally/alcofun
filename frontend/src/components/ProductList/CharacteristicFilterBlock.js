import { useState, useEffect } from "react";
import fetchJson from "../../utils/http-utils.js";
import Accordion from "react-bootstrap/Accordion";
import { Spinner } from "react-bootstrap";
import FilterBody from "./FilterBody";

const CharacteristicFilterBlock = (props) => {
  const [characterisitcs, setCharacterisitcs] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const typeId = props.id;

  async function fetchCharacteristics() {
    setIsLoading(true);

    const json = await fetchJson(`characteristic/${typeId}`);
    setCharacterisitcs(
      json.map((item) => {
        return { id: item.id, name: item.value };
      })
    );

    setIsLoading(false);
  }

  useEffect(() => {
    fetchCharacteristics();
  }, []);

  return (
    <Accordion.Item eventKey={typeId + 2}>
      <Accordion.Header>{`${props.name}${
        props.uom !== null ? ", " + props.uom : ""
      }`}</Accordion.Header>
      <Accordion.Body>
        {isLoading && <Spinner />}
        {!isLoading && (
          <FilterBody
            items={characterisitcs}
            type="characteristic"
            onChange={props.onChange}
            enabledItems={props.enabledCharacteristics}
          />
        )}
      </Accordion.Body>
    </Accordion.Item>
  );
};

export default CharacteristicFilterBlock;
