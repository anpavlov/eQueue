import unittest
import sys

from tests.api import ApiTestCase

if __name__ == '__main__':
    suite = unittest.TestSuite((
        unittest.makeSuite(ApiTestCase),
    ))
    result = unittest.TextTestRunner().run(suite)
    sys.exit(not result.wasSuccessful())