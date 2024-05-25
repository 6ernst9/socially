import React, {useEffect, useState} from "react";
import {ReactComponent as Search} from '../../../assets/icons/search.svg';
import {useDispatch} from "react-redux";
import {searchByTerm} from "../../../widgets/messaging-overview-widget/model/effects";

const SearchBar: React.FC = () => {
    const [search, setSearch] = useState('');
    const dispatch = useDispatch();

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
    }
    
    useEffect(() => {
        //searchByTerm({term: search, dispatch});
    }, [dispatch, search]);

    return (
        <div className="search-bar">
            <Search/>
            <input
                className='search-bar-form'
                placeholder='Search'
                type='text'
                onChange={handleSearchChange}/>
        </div>
    )
}

export default SearchBar;