
FROM nginx:alpine

ADD ./docker/ /etc/nginx/
# Clears the static files
RUN rm -rf /usr/share/nginx/html
# Copy the static web content
ADD ./build /usr/share/nginx/html

# Suggests to bind port 80 to any port of the host system
EXPOSE 80
