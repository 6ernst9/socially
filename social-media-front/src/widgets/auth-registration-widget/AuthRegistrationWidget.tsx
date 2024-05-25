import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate} from "react-router-dom";
import BText from "../../components/core/BText/BText";
import {BACKGROUND_DARK, PRIMARY_LIGHT} from "../../utils/constants";
import Button from "../../components/core/Button/Button";
import LText from "../../components/core/LText/LText";
import Line from "../../components/core/Line/Line";
import Credits from "../../components/core/Credits/Credits";
import {register} from "./model/effects";
import {getSession} from "../auth-login-widget/model/effects";
import {sessionSelect} from "../../redux/core/session/selectors";
import {changePage, registrationSuccess} from "../auth-login-widget/model/reducers";
import {authSelect} from "../auth-login-widget/model/selectors";
import {ReactComponent as Logo} from "../../assets/icons/logo.svg";

const AuthRegistrationWidget: React.FC = () => {
    const errorMessage = useSelector(authSelect.authError);
    const isLogged = useSelector(authSelect.isLogged);
    const session = localStorage.getItem('session');
    const id = useSelector(sessionSelect.id);

    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [email, setEmail] = useState<string>('');
    const [fullName, setFullName] = useState<string>('');
    const [phoneNumber, setPhoneNumber] = useState<string>('');

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        if (isLogged || errorMessage === 'NO-ERROR') {
            dispatch(registrationSuccess(session || ''));
            navigate('/home');
        }

        if(id !== '') {
            getSession({id, dispatch});
        }
    }, [dispatch, errorMessage, navigate]);
    
    useEffect(() => {
        dispatch(changePage());
    }, [dispatch])

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => setEmail(event.target.value);
    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => setPassword(event.target.value);
    const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => setUsername(event.target.value);
    const handleFullNameChange = (event: React.ChangeEvent<HTMLInputElement>) => setFullName(event.target.value);
    const handlePhoneNumberChange = (event: React.ChangeEvent<HTMLInputElement>) => setPhoneNumber(event.target.value);
    const handleSubmit = async() => {
        console.log(phoneNumber);
        await register({
            email, username, password, fullName, phoneNumber, dispatch
        });
    }

    return (
        <div className="auth-registration-back">
            <div className="auth-container">
                <div className="auth-container-header">
                    <Logo/>
                    <p className="auth-title">Sign up to Socially</p>
                </div>

                <div className="auth-forms">
                    <input
                        className="auth-form"
                        placeholder="Full name"
                        type='text'
                        onChange={handleFullNameChange}
                        onKeyPress={event => {
                            if (event.key === 'Enter') {
                                handleSubmit();
                            }
                        }}/>
                    <input
                        className="auth-form"
                        placeholder="Email"
                        type='email'
                        onChange={handleEmailChange}
                        onKeyPress={event => {
                            if (event.key === 'Enter') {
                                handleSubmit();
                            }
                        }}/>
                    <input
                        className="auth-form"
                        placeholder="Phone number"
                        type='text'
                        onChange={handlePhoneNumberChange}
                        onKeyPress={event => {
                            if (event.key === 'Enter') {
                                handleSubmit();
                            }
                        }}/>
                    <input
                        className="auth-form"
                        placeholder="Username"
                        type='text'
                        onChange={handleUsernameChange}
                        onKeyPress={event => {
                        if (event.key === 'Enter') {
                            handleSubmit();
                        }
                    }}/>
                    <input
                        className="auth-form"
                        placeholder="Password"
                        type='password'
                        onChange={handlePasswordChange}
                        onKeyPress={event => {
                            if (event.key === 'Enter') {
                                handleSubmit();
                            }
                        }}
                    />

                </div>
                <Button content='Sign up' onClick={handleSubmit}/>
                {errorMessage && errorMessage !== 'NO-ERROR' && <LText text={errorMessage} color={'#ff0000'}/>}
                <Line/>
                <div className="auth-under">
                    <LText text="Already have an account?"/>
                    <Link to='/login' className='link-btn'>
                        <BText text="Log in" color={PRIMARY_LIGHT} />
                    </Link>
                </div>
            </div>
            <Credits color={BACKGROUND_DARK}/>
        </div>
    )
}

export default AuthRegistrationWidget;