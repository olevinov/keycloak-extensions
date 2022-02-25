local log = require("log")
local json = require('json')

local server = require('http.server').new(nil, 8089)

local function handler(req)
    log.info('handler')
    local email = req:post_param("email")
    if email == nil then
        local err = 'no email param'
        log.info('ERROR: ' .. err)
        return {body = json.encode({status = "Error!", error = err}), status = 400}
    end
    log.info('email = ' .. email)
    return {body = json.encode({status = "Success!"}), status = 200}
end

server:route({ path = '/' }, handler)

server:start()
