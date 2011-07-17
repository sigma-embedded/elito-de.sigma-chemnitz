#ifndef OE_AVUTIL_SHA1_H
#define OE_AVUTIL_SHA1_H

#include "sha.h"

#define AVSHA1		AVSHA
#define av_sha1_size	av_sha_size

inline static void av_sha1_init(struct AVSHA1* context)
{
	av_sha_init(context, 160);
}

inline static void av_sha1_update(struct AVSHA1* context, const uint8_t* data, unsigned int len)
{
	av_sha_update(context, data, len);
}

inline static void av_sha1_final(struct AVSHA1* context, uint8_t digest[20])
{
	av_sha_final(context, digest);
}



#endif OE_AVUTIL_SHA1_H
