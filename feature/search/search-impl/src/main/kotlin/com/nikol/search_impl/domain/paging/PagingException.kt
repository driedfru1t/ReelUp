package com.nikol.search_impl.domain.paging

import com.nikol.domainutil.BaseResponseError

class PagingException(val error: BaseResponseError) : Exception()