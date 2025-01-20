Second Hand Project
- the idea of this project is to make a wed application that let users can buy and sell second hand products.
- Deployed Link : https://dyyy3fcos7nl0.cloudfront.net/home 

-------------Back-end-----------
- Tech stack: java 17, Spring boot 3, Mysql, Postman, Maven
- github: https://github.com/caothien231/second-hand-project
- Use JWT to authentication and authorization, Role-Based Access Control, Password Hashing
- Use google firebas to store product images uploaded from user.
- Use SMTP for email hosting (sent confirmation email when user sign-up, buy products) 

- Backend Setup Local:
    +Clone the repository.
    +Install dependencies using mvn install.
    +Run the backend server using mvn spring-boot:run
    +The server runs on http://localhost:8005.
- Deployed on AWS EC2, RDS

-------------Front-end-----------
- Tech stack: javaScript, React-js, BootStrap, nodeJs
- github: https://github.com/caothien231/second-hand-project-front-end
- js-cookie for storing JWT tokens securely in cookies.
- axios for making API calls to the backend.
- Bootstrap and React-Bootstrap for styling.

- Frontend Setup Local:
    +Clone the repository.
    +Install dependencies using npm install.
    +Run the React app using npm start.
    +The app runs on http://localhost:3000.
- Deployed on AWS S3, CloudFront.


- Home Page
  ![image](https://github.com/user-attachments/assets/dfe6dd8f-0988-4417-8c1d-5f19e7e0c9c9)
- Producr Detail Page
![image](https://github.com/user-attachments/assets/656daeac-5377-45c8-8943-3498cf8da639)
- Upload Product Page
  ![image](https://github.com/user-attachments/assets/b9e9a7f2-00bc-4709-9c90-95a504dd6225)
