// jest.config.js
module.exports = {
  // ...other jest configuration
  reporters: [
    "default",
    ["jest-junit", {
      outputDirectory: "reports",
      outputName: "junit.xml"
    }]
  ]
}
