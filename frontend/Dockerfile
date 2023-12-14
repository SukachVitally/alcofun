
FROM nginx:alpine

ADD ./docker/ /etc/nginx/
RUN rm -rf /usr/share/nginx/html
ADD ./build /usr/share/nginx/html

EXPOSE 80
