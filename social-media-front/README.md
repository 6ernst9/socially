# YOLO Social Media
©2024 YOLO from ROBERT ERNST. All rights reserved.

# Overview 
This project is the frontend part of a Social Media application designed to offer users a platform for sharing experiences, connecting with others, and engaging in real-time conversations. The application is built using React, leveraging modern practices and technologies to ensure a responsive, user-friendly interface.
 
# Key Features

## User Authentication
Secure login and registration process, with session management to maintain user state. User authentication state is managed via Redux, with JWT tokens stored in local storage for session persistence.

## Profile Management
Users can view and edit their profiles, which includes personal information, profile descriptions, and user statistics like followers and following counts. The profile state is handled in Redux, providing a centralized state management solution.

## Real-Time Messaging
A core feature of the app is real-time messaging, allowing users to instantly send and receive messages. This is implemented using WebSockets and RxJS, providing an efficient and reactive approach to handle real-time data streams.

## Responsive Design
The application is designed to be responsive and accessible on various devices and screen sizes, ensuring a seamless user experience across platforms.

# Technologies used
* React used for building the user interface.
* Redux for state management, especially for handling authentication state and profile information.
* RxJS utilized in conjunction with WebSockets to handle real-time messaging features.
* CSS/SCSS for styling the application, ensuring a modern and responsive design.
* Axios for making API requests to the backend server.
* WebSocket used for real-time bidirectional communication in the messaging feature.

# Getting Started with local environment

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

# Project structure
'/src'
* '/components' for reusable UI components.
* '/redux'  for redux state management core and global slices and actions.
* '/assets' for static like images and fonts.
* '/widgets' for page components used in routing.
* * '/tests' for all the tests ( unit, integration, visual) related to the page
* * '/model' which contains a set of important files:

'effects.ts' executes all the requests and calls related to the page 

'types.ts' contains all the types included in the page

'reducers.ts' contains all the dispatch functions for the page's state

'selectors.ts' contains all the select functions for the page's state

'defaultState.ts' contains all the default state for the page's state

App.tsx is the main application component


## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
