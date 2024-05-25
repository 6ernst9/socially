import React, {useEffect, useState} from "react";
import LText from "../../components/core/LText/LText";
import {BACKGROUND_DARK, PRIMARY_LIGHT} from "../../utils/constants";
import Line from "../../components/core/Line/Line";
import BText from "../../components/core/BText/BText";
import './styles.css';
import {getSession, login} from "./model/effects";
import {useDispatch, useSelector} from "react-redux";
import {authSelect} from "./model/selectors";
import {Link, useNavigate} from "react-router-dom";
import Credits from "../../components/core/Credits/Credits";
import Button from "../../components/core/Button/Button";
import {sessionSelect} from "../../redux/core/session/selectors";
import {changePage, loginSuccess} from "./model/reducers";
import {ReactComponent as Logo} from "../../assets/icons/logo.svg";

const AuthLoginWidget: React.FC = () => {
    const errorMessage = useSelector(authSelect.authError);
    const isLogged = useSelector(authSelect.isLogged);
    const session = localStorage.getItem('session');
    const id = useSelector(sessionSelect.id);

    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if (isLogged || errorMessage === 'NO-ERROR') {
            dispatch(loginSuccess(session || ''));
            navigate('/home');
        }
    }, [dispatch, errorMessage, isLogged, id]);
    
    useEffect(() => {
        if(id !== '') {
            getSession({id, dispatch});
        }

        dispatch(changePage());
    }, [dispatch])

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => setUsername(event.target.value)
    const handlePassChange = (event: React.ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)

    const handleSubmit = async () => {
        await login({username, password, dispatch});
    };

    return (
        <div className="auth-login-back">
            <div className="auth-container">
                <div className="auth-container-header">
                    <Logo/>
                    <p className="auth-title">Log in to Socially</p>
                </div>
                <div className="auth-forms">
                     <input
                            className="auth-form"
                            placeholder="Phone number, username or email"
                            type='email'
                            onChange={handleEmailChange}
                            onKeyPress={event => {
                                if (event.key === 'Enter') {
                                    handleSubmit();
                                }
                            }}/>
                     <input
                            className="auth-form"
                            placeholder="Password"
                            type='password'
                            onChange={handlePassChange}
                            onKeyPress={event => {
                                if (event.key === 'Enter') {
                                    handleSubmit();
                                }
                            }}/>
                </div>

                <Link to='resetpass' className='link-btn'>
                    <BText text="Forgot password?" color={PRIMARY_LIGHT}/>
                </Link>
                <Button content='Log in' onClick={handleSubmit}/>
                {errorMessage && errorMessage !== 'NO-ERROR' && <LText text={errorMessage} color={'#ff0000'}/>}
                <Line/>
                <div className="auth-under">
                    <LText text="Don't have an account?"/>
                    <Link to='/register' className='link-btn'>
                        <BText text="Sign Up" color={PRIMARY_LIGHT} />
                    </Link>
                </div>
            </div>
            <Credits color={BACKGROUND_DARK}/>
        </div>
    )
}

export default AuthLoginWidget;