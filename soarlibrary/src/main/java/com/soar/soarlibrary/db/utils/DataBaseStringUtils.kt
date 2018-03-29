/*
 * Copyright (C) 2016 Lusfold
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soar.soarlibrary.db.utils


object DataBaseStringUtils {

    /**
     * @param str
     * @return if str is null return true,else return false.
     */
    fun isNull(str: String?): Boolean {
        return if (str == null) true else false
    }

    /**
     * @param str
     * @return if str is not null return ture,else return false.
     */
    fun isNotNull(str: String): Boolean {
        return !isNull(str)
    }

    /**
     * @param str
     * @return if str is null or equals "" return true,else return false.
     */
    fun isBlank(str: String): Boolean {
        if (isNull(str))
            return true
        return if (str == "") true else false
    }

    /**
     * @param str
     * @return if str is not null and do not equals "" return true,else return false.
     */
    fun isNotBlank(str: String): Boolean {
        return !isBlank(str)
    }

}
