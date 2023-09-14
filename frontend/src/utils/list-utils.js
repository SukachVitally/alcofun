function updateList(list, id, checked) {
    let set = new Set(list);
    if (checked) {
        set.add(id);
    } else {
        set.delete(id);
    }
    return [...set];
}

export default updateList;
