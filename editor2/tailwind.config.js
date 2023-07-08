module.exports = {
    content: ["./src/**/*", "./public/index.html"],
    theme: {
        extend: {}
    },
    plugins: [
        require('@tailwindcss/typography'),
        require('@tailwindcss/forms'),
        require('@tailwindcss/line-clamp')
    ]
}