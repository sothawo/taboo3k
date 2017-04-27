/*
 Copyright 2015 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.taboo3.mvc

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.streams.asSequence

/**
 * Service to provide User details.

 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Component
class Taboo3UserService : UserDetailsService {


    /** configured user file  */
    @Value("\${taboo3.users}")
    private val filename: String? = null

    /** Map with known users. Key is the username, password ist hashed  */
    private val knownUsers = mutableMapOf<String, User>()

    /**
     * returns the UserDetails for a username
     * @param username the username
     * @throws UsernameNotFoundException if the username is not found
     */
    override fun loadUserByUsername(username: String): UserDetails {
        var configuredUser: User? = null

        synchronized(knownUsers) {
            configuredUser = knownUsers[username]

            if (null == configuredUser) {
                // reload the data from users file
                log.debug("loading user data")
                knownUsers.clear()
                if (filename != null) {
                    try {
                        log.debug("user file: {}", filename)

                        Files.lines(Paths.get(filename)).asSequence()
                                .map(String::trim)
                                .filter { !it.isEmpty() }
                                .filter { !it.startsWith("#") }
                                .forEach {
                                    val fields = it.split(":")
                                    if (fields.size == 3) {
                                        val user = fields[0]
                                        val hashedPassword = fields[1]
                                        var roles = fields[2].split(",")
                                        if (roles.isEmpty()) {
                                            roles = listOf("undef")
                                        }
                                        val authorities = roles.mapTo(ArrayList<GrantedAuthority>()) { SimpleGrantedAuthority(it) }
                                        knownUsers.put(user, User(user, hashedPassword, authorities))
                                    }
                                }
                        log.debug("loaded {} user(s)", knownUsers.size)
                    } catch(e: Exception) {
                        log.debug("reading file", e)
                    }
                }

                // search again after reload
                configuredUser = knownUsers[username]
            }
        }
        // need to return a copy as Spring security erases the password in the object after verification
        val user = configuredUser ?: throw UsernameNotFoundException(username)
        return User(user.username, user.password, user.authorities)
    }

    /**
     * encodes the string that is given as argument.

     * @param args
     * *         program arguments.
     */
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            System.out.printf(BCryptPasswordEncoder().encode(args[0]))
        }
    }

    companion object {
        /** Logger for the class  */
        private val log = LoggerFactory.getLogger(Taboo3UserService::class.qualifiedName)
    }
}
