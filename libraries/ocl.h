#include <ctype.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <limits.h>
#include <stdarg.h>

#define ALLOCATIONSIZE 50

#define TRUE 1
#define FALSE 0

/* This header provides ANSI C implementations of
   OCL library operations. OCL indexing conventions
   are used to access elements of collections and
   strings: numbering starts from 1 instead of 0.

   Note that all parameters may be regarded as const
   except in the case of treesort and modifying tree
   operations.

   Sets and sequences are represented as arrays, terminated
   by NULL. 
   NULL collections are treated as being empty collections. 
   Collections are assumed not to contain NULL. 

   Collections of primitive types Sequence(T), etc are 
   represented as T**, collections of Strings as char** and 
   collections of objects of class E as struct E**.  
*/

long long getTime(void)
{ return (long long) time(NULL)*1000; }

int length(void* col[])
{ int result = 0;
  if (col == NULL)  
  { return result; }

  void* ex = col[0];
  while (ex != NULL)
  { result++;
    ex = col[result];
  }
  return result;
}

unsigned char isIn(void* x, void* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  void* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (ex == x) 
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}

unsigned char isInint(int x, int* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  int* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (*ex == x) 
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}

unsigned char isInlong(long x, long* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  long* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (*ex == x) 
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}

unsigned char isIndouble(double x, double* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  double* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (*ex == x) 
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}

unsigned char isInString(char* x, char* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  char* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (strcmp(x,ex) == 0)
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}

unsigned char isInboolean(unsigned char x, unsigned char* col[])
{ int result = FALSE;
  if (col == NULL) 
  { return result; }

  unsigned char* ex = col[0];
  int i = 0;
  while (ex != NULL)
  { if (*ex == x) 
    { return TRUE; }
    i++;
    ex = col[i];
  }
  return result;
}



unsigned char isEmpty(void* col[])
{ int result = TRUE; 
  void* ex = col[0];
  if (ex != NULL) 
  { return FALSE; } 
  return result; 
} 

unsigned char notEmpty(void* col[])
{ int result = FALSE; 
  void* ex = col[0];
  if (ex != NULL) 
  { return TRUE; } 
  return result; 
} 

void* any(void* col[])
{ if (col == NULL) 
  { return NULL; }
  return col[0];
}

void* at(void* col[], int i)
{ int n = length(col);
  if (n == 0 || i < 1 || i > n) 
  { return NULL; }
  return col[i-1];
}

char* atStrings(char* ss, int i)
{ int n = strlen(ss);
  if (n == 0 || i < 1 || i > n) 
  { return NULL; }
  char* res = (char*) calloc(2, sizeof(char)); 
  res[0] = ss[i-1];
  res[1] = '\0'; 
  return res; 
}

int atint(int* col[], int i)
{ int n = length(col);
  if (n == 0 || i < 1 || i > n) 
  { return 0; }
  return *col[i-1];
}

long atlong(long* col[], int i)
{ int n = length(col);
  if (n == 0 || i < 1 || i > n) 
  { return 0; }
  return *col[i-1];
}

double atdouble(double* col[], int i)
{ int n = length(col);
  if (n == 0 || i < 1 || i > n) 
  { return 0; }
  return *col[i-1];
}

unsigned char atboolean(unsigned char* col[], int i)
{ int n = length(col);
  if (n == 0 || i < 1 || i > n) 
  { return FALSE; }
  return *col[i-1];
}



void* first(void* col[])
{ if (col == NULL) 
  { return NULL; }
  return col[0];
}

void* last(void* col[])
{ int n = length(col);
  if (n == 0) 
  { return NULL; }
  return col[n-1];
}

int lastint(int* col[])
{ int n = length(col);
  if (n == 0) 
  { return 0; }
  return *col[n-1];
}

  int** frontint(int* col[])
  { int** result;
    int len = length((void**) col);
    result = (int**) calloc(len + 1, sizeof(int*));
    int i = 0;
    for ( ; i < len - 1; i++)
    { result[i] = col[i]; }
  
    result[len] = NULL;
    return result;
  }



char* concatenateStrings(const char* st1, const char* st2)
{ if (st1 == NULL)
  { return st2; } 
  if (st2 == NULL) 
  { return st1; }

  int n = strlen(st1);
  int m = strlen(st2);
  char* result = (char*) calloc(n + m + 1, sizeof(char));
  strcat(strcat(result, st1), st2);
  result[n+m] = '\0';
  return result;
}


unsigned char containsAll(void* col1[], void* col2[])
{ /* col2 <: col1 */
  int n2 = length(col2);
  unsigned char result = TRUE;
  int i = 0;
  for ( ; i < n2; i++)
  { void* ex = col2[i];
    if (ex == NULL) { return result; }
    if (isIn(ex,col1)) { }
    else { return FALSE; }
  }
  return result;
}

 unsigned char equalsSet(void* col1[], void* col2[])
 { return containsAll(col1,col2) && containsAll(col2,col1); }

 unsigned char equalsSequence(void* col1[], void* col2[])
 { unsigned char result = TRUE;
   int n1 = length(col1);
   int n2 = length(col2);
   if (n1 != n2) 
   { return FALSE; }
   int i = 0;
   for ( ; i < n1; i++)
   { void* ex = col1[i];
     if (ex == NULL) 
     { return result; }
     if (ex == col2[i]) { }
     else 
     { return FALSE; }
   }
   return result;
 }


 unsigned char disjoint(void* col1[], void* col2[])
 { int n = length((void**) col1);
   unsigned char result = TRUE;
   int i = 0;
   for ( ; i < n; i++)
   { void* ex = col1[i];
     if (ex == NULL) 
     { return result; }
     if (isIn(ex,col2)) 
     { return FALSE; }
   }
   return result;
 }

char* byte2char(int x)
{ char* arr = " "; 
  arr[0] = x; 
  return arr; 
}

int char2byte(char* str)
{ if (strlen(str) == 0)
  { return -1; } 
  return (int) str[0];
} 

int oclFloor(double x)
{ return (int) floor(x); } 

int oclRound(double x)
{ int f = (int) floor(x);
  int c = (int) ceil(x);
  if (x >= f + 0.5) { return c; }
  return f;
}

int oclCeil(double x)
{ return (int) ceil(x); }

double cbrt(double x)
{ return pow(x, 1.0/3); }

long gcd(long xx, long yy)
{ long x = labs(xx); 
  long y = labs(yy); 

  while (x != 0 && y != 0)
  { long z = y; 
    y = x % y; 
    x = z; 
  } 

  if (y == 0)
  { return x; } 

  if (x == 0)
  { return y; } 

  return 0; 
} 

double roundTo(double x, int n)
{ if (n < 0)
  {
    return round(x);
  }
  double y = x * pow(10, n);
  return round(y) / pow(10, n);
}

double truncateTo(double x, int n)
{ if (n < 0)
  {
    return (int) x;
  }
  double y = x * pow(10, n);
  return ((int) y) / pow(10, n);
}


char* toLowerCase(const char* s)
{ int n = strlen(s);
  char* result = (char*) calloc(n+1,sizeof(char));
  int i = 0;
  for ( ; i < n; i++)
  { result[i] = tolower(s[i]); }
  result[n] = '\0';
  return result;
}

char* toUpperCase(const char* s)
{ int n = strlen(s);
  char* result = (char*) calloc(n+1,sizeof(char));
  int i = 0;
  for ( ; i < n; i++)
  { result[i] = toupper(s[i]); }
  result[n] = '\0';
  return result;
}

char* subStrings(const char* s, int i, int j)
{ /* Characters of s from s[i-1] to s[j-1], inclusive */
  int n = strlen(s);
  if (i < 1) { i = 1; } 
  if (j > n) { j = n; } 
  if (i > j) { return ""; }
  char* result = (char*) calloc(j-i+2,sizeof(char));

  int x = i-1;
  for ( ; x < j; x++)
  { result[x-i+1] = s[x]; }
  result[j-i+1] = '\0';
  return result;
}

char* subStringToEnd(const char* s, int i)
{ /* Characters of s from s[i-1] to strlen(s)-1, inclusive */
  int n = strlen(s);
  return subStrings(s,i,n); 
}

unsigned char equalsIgnoreCase(char* s1, char* s2)
{ unsigned char result = FALSE;
  if (strcmp(toLowerCase(s1), toLowerCase(s2)) == 0)
  { result = TRUE; }
  return result;
}

char* firstStrings(const char* s)
{ return subStrings(s,1,1); }

char* lastStrings(const char* s)
{ int n = strlen(s);
  return subStrings(s,n,n);
}

char* frontStrings(const char* s)
{ int n = strlen(s);
  return subStrings(s,1,n-1);
}

char* tailStrings(const char* s)
{ int n = strlen(s);
  return subStrings(s,2,n);
}

unsigned char hasCharacter(char* str, char x)
{ int result = FALSE;
  if (str == NULL) 
  { return result; }
  char ex = str[0];
  int i = 0;
  while (ex != '\0')
  { if (ex == x) 
    { return TRUE; }
    i++;
    ex = str[i];
  }
  return result;
}

  unsigned char startsWith(const char* s, const char* sb)
  { int n = strlen(s);
    int m = strlen(sb);
    if (m <= n && strncmp(s,sb,m) == 0)
    { return TRUE; }
    return FALSE;
  }

  unsigned char endsWith(const char* s, const char* sb)
  { int n = strlen(s);
    int m = strlen(sb);
    if (m <= n && strcmp(s + (n-m),sb) == 0)
    { return TRUE; }
    return FALSE;
  }

  int indexOfStrings(const char* s, const char* sb)
  { int n = strlen(s);
    int m = strlen(sb);
    int result = 0;
    if (n == 0 || m == 0) 
    { return result; }
    int i = 0;
    for ( ; i < n; i++)
    { if (startsWith(&s[i],sb))
      { return i+1; }
    }
    return result;
  }

char* before(char* s, char* sep)
{ if (strlen(sep) == 0) 
  { return s; }
  char* ind = strstr(s,sep); 
  if (ind == NULL)
  { return s;} 
  char* res = (char*) calloc(strlen(s),sizeof(char)); 
  return strncpy(res,s,(ind-s)); 
}

char* after(char* s, char* sep)
{ if (strlen(sep) == 0) 
  { return ""; }
  char* ind = strstr(s,sep); 
  if (ind == NULL)
  { return "";} 
  char* res = (char*) calloc(strlen(s),sizeof(char)); 
  return strcpy(res,ind + strlen(sep)); 
}

  int count(void* x, void* col[])
  { int n = length(col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { if (x == col[i]) 
      { result++; } 
    }
    return result;
  }

  int countString(const char* x, const char* col[])
  { int n = length((void**) col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { char* y = col[i];
      if (strcmp(x,y) == 0) 
      { result++; }
    }
    return result;
  }

  int countint(const int x, const int* col[])
  { int n = length((void**) col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { int* y = col[i];
      if (x == *y) 
      { result++; }
    }
    return result;
  }

  int countlong(const long x, const long* col[])
  { int n = length((void**) col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { long* y = col[i];
      if (x == *y) 
      { result++; }
    }
    return result;
  }

  int countdouble(const double x, const double* col[])
  { int n = length((void**) col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { double* y = col[i];
      if (x == *y) 
      { result++; }
    }
    return result;
  }

  int countStrings(const char x, const char* col)
  { int n = strlen(col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { char y = col[i];
      if (x == y) 
      { result++; }
    }
    return result;
  }

  char* reverseStrings(const char* s)
  { int n = strlen(s);
    char* result = (char*) calloc(n+1,sizeof(char));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = '\0'; 
    return result;
  }

int lastIndexOf(char* s, char* d)
{ int result = 0;
  int lens = strlen(s);
  int lend = strlen(d); 

  if (lens == 0 || lend == 0) 
  { return 0; } 

  int i = indexOfStrings(reverseStrings(s), reverseStrings(d));  
  if (i <= 0)
  { result = 0; }
  else
  { if (i > 0)
    { result = lens - i - lend + 2; }
  }
  return result;
}

char* trim(char* str)
{ char* result = ""; 
  int i = 1;
  int n = strlen(str); 
  while (i < n && (str[i-1] == ' ' || str[i-1] == '\n' || 
                   str[i-1] == '\t'))
  { i = i + 1; }

  int j = n;

  while (j >= 1 && 
    (str[j-1] == ' ' || str[j-1] == '\n' || str[j-1] == '\t')) 
  { j = j - 1; }

  if (j < i)
  { result = ""; }
  else
  { result = subStrings(str, i, j); }
  return result;
}

  char** characters(char* s)
  { int n = strlen(s);
    char** result = (char**) calloc(n+1, sizeof(char*));
    int i = 0;
    for ( ; i < n; i++)
    { char* x = (char*) calloc(2, sizeof(char));
      x[0] = s[i];
      x[1] = '\0';
      result[i] = x;
    }
    result[n] = NULL;
    return result;
  }

  int** bytesOf(char* s)
  { int n = strlen(s);
    int** result = (int**) calloc(n+1, sizeof(int*));
    int i = 0;
    for ( ; i < n; i++)
    { int* x = (int*) malloc(sizeof(int));
      *x = s[i]; 
      result[i] = x; 
    }
    result[n] = NULL;
    return result;
  }

char* replace(char* str, char* delim, char* s2)
{ char* result = ""; 
  int i = indexOfStrings(str, delim);
  while (i > 0)
  { result = concatenateStrings(result, 
         concatenateStrings(subStrings(str, 1, i - 1), s2));
    str = subStrings(str, i + strlen(delim), strlen(str));
    i = indexOfStrings(str, delim);
  }
  result = concatenateStrings(result, str);
  return result;
}

  char* subtractStrings(const char* s, const char* s1)
  { /* Characters of s not in s1 */
    int n = strlen(s);
    char* result = (char*) calloc(n+1,sizeof(char));
    int i = 0;
    int j = 0;
    for ( ; i < n; i++)
    { char c = s[i];
      if (strchr(s1,c) == NULL)
      { result[j] = c;
        j++;
      }
    }
    result[j] = '\0';
    return result;
  }


  int indexOfSequence(void** col, void* x)
  { int n = length(col);
    int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { if (col[i] == x)
      { return i+1; }
    }
    return result;
  }

  void** reverseSequence(void** s)
  { int n = length(s);
    void** result = (void**) calloc(n+1,sizeof(void*));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = NULL; 
    return result;
  }

int lastIndexOfSequence(void** col, void* x)
{ int n = length(col); 

  if (n == 0) 
  { return 0; } 

  void** revcol = reverseSequence(col); 

  int i = indexOfSequence(revcol, x);  
  if (i <= 0)
  { return 0; }
  else
  { return n - i + 1; }
}


  unsigned char hasPrefixSequence(void** sq, void** sq1)
  { int n = length(sq);
    int m = length(sq1);
    if (n == 0 || m == 0 || m > n)
    { return FALSE; }

    int i = 0;
    for ( ; i < m; i++) 
    { if (sq[i] == sq1[i]) { } 
      else 
      { return FALSE; } 
    } 

    return TRUE;
  }

  int indexOfSubSequence(void** sq, void** sq1)
  { int n = length(sq);
    int m = length(sq1);
    if (n == 0 || m == 0 || m > n)
    { return 0; }
  
    int i = 0; 
    for ( ; i+m <= n; i++) 
    { if (hasPrefixSequence(&sq[i], sq1))
      { return i+1; } 
    } 
    return 0; 
  }

int lastIndexOfSubSequence(void** sq, void** sq1)
{ int n = length(sq); 
  int m = length(sq1); 

  if (n == 0 || m == 0 || m > n) 
  { return 0; } 

  void** revsq = reverseSequence(sq); 
  void** revsq1 = reverseSequence(sq1); 

  int i = indexOfSubSequence(revsq, revsq1);  
  if (i <= 0)
  { return 0; }
  else
  { return n - i - m + 2; }
}

 void** append(void* col[], void* ex)
 { void** result;
   int len = length(col);
   if (len % ALLOCATIONSIZE == 0)
   { result = (void**) calloc(len + ALLOCATIONSIZE + 1, sizeof(void*));
     int i = 0;
     for ( ; i < len; i++)
     { result[i] = col[i]; }
   }
   else
   { result = col; }
   result[len] = ex;
   result[len+1] = NULL;
   return result;
 }

 void** appendOclAnyint(void* col[], int ex)
 { int* ptr = malloc(sizeof(int)); 
   *ptr = ex; 
   return append(col,ptr); 
 }

 void** appendOclAnyboolean(void* col[], unsigned char ex)
 { unsigned char* ptr = malloc(sizeof(unsigned char)); 
   *ptr = ex; 
   return append(col,ptr); 
 }

 void** appendOclAnylong(void* col[], long ex)
 { long* ptr = malloc(sizeof(long)); 
   *ptr = ex; 
   return append(col,ptr); 
 }

 void** appendOclAnydouble(void* col[], double ex)
 { double* ptr = malloc(sizeof(double)); 
   *ptr = ex; 
   return append(col,ptr); 
 }

 void** appendOclAnyString(void* col[], char* ex)
 { return append(col, (void*) ex); }


  

  void** prepend(void* col[], void* ex)
  { void** result;
    int len = length(col);
    result = (void**) calloc(len + ALLOCATIONSIZE + 1, sizeof(void*));
    int i = 0;
    result[0] = ex; 
    i++; 
    for ( ; i <= len; i++)
    { result[i] = col[i-1]; }
    result[len+1] = NULL;
    return result;
  }

/* These functions support lists of primitive types, strings, numerics and booleans */

  char** appendString(char* col[], char* ex)
  { char** result;
    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (char**) calloc(len + ALLOCATIONSIZE + 1, sizeof(char*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }
    result[len] = ex;
    result[len+1] = NULL;
    return result;
  }

  char** prependString(char* col[], char* ex)
  { char** result;
    int len = length(col);
    result = (char**) calloc(len + ALLOCATIONSIZE + 1, sizeof(char*));
    int i = 0;
    result[0] = ex; 
    i++; 
    for ( ; i <= len; i++)
    { result[i] = col[i-1]; }
    result[len+1] = NULL;
    return result;
  }

char** selectString(char* col[], unsigned char (*fe)(char*))
{ int n = length((void**) col);
  char** result = (char**) calloc(n+1, sizeof(char*));
  int i = 0;
  int j = 0; 
  for ( ; i < n; i++)
  { char* self = col[i];
    if ((*fe)(self))
    { result[j] = self;
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

char** rejectString(char* col[], unsigned char (*fe)(char*))
{ int n = length((void**) col);
  char** result = (char**) calloc(n+1, sizeof(char*));
  int i = 0;
  int j = 0; 
  for ( ; i < n; i++)
  { char* self = col[i];
    if ((*fe)(self)) { } 
    else 
    { result[j] = self;
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

void** collectString(char* col[], void* (*fe)(char*))
{ int n = length((void**) col);
  void** result = (void**) calloc(n+1, sizeof(void*));
  int i = 0;
  for ( ; i < n; i++)
  { char* self = col[i];
    result[i] = (*fe)(self);
  }
  result[n] = NULL;
  return result;
}

int** selectint(int* col[], unsigned char (*fe)(int))
{ int n = length((void**) col);
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0;
  int j = 0; 
  for ( ; i < n; i++)
  { int* self = col[i];
    if ((*fe)(*self))
    { result[j] = self;
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

int** rejectint(int* col[], unsigned char (*fe)(int))
{ int n = length((void**) col);
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0;
  int j = 0; 
  for ( ; i < n; i++)
  { int* self = col[i];
    if ((*fe)(*self)) { } 
    else 
    { result[j] = self;
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

void** collectint(int* col[], void* (*fe)(int))
{ int n = length((void**) col);
  void** result = (void**) calloc(n+1, sizeof(void*));
  int i = 0;
  for ( ; i < n; i++)
  { int self = *col[i];
    result[i] = (*fe)(self);
  }
  result[n] = NULL;
  return result;
}

int** collectintint(int* col[], int (*fe)(int))
{ int n = length((void**) col);
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0;
  for ( ; i < n; i++)
  { int self = *col[i];
    int* y = (int*) malloc(sizeof(int)); 
    *y = (*fe)(self);
    result[i] = y;
  }
  result[n] = NULL;
  return result;
}

unsigned char** collectintboolean(int* col[], unsigned char (*fe)(int))
{ int n = length((void**) col);
  unsigned char** result = (unsigned char**) calloc(n+1, sizeof(unsigned char*));
  int i = 0;
  for ( ; i < n; i++)
  { int self = *col[i];
    unsigned char* y = (unsigned char*) malloc(sizeof(unsigned char)); 
    *y = (*fe)(self);
    result[i] = y;
  }
  result[n] = NULL;
  return result;
}

unsigned char** collectbooleanboolean(unsigned char* col[], unsigned char (*fe)(unsigned char))
{ int n = length((void**) col);
  unsigned char** result = (unsigned char**) calloc(n+1, sizeof(unsigned char*));
  int i = 0;
  for ( ; i < n; i++)
  { unsigned char self = *col[i];
    unsigned char* y = (unsigned char*) malloc(sizeof(unsigned char)); 
    *y = (*fe)(self);
    result[i] = y;
  }
  result[n] = NULL;
  return result;
}

char** collectintString(int* col[], char* (*fe)(int))
{ int n = length((void**) col);
  char** result = (char**) calloc(n+1, sizeof(char*));
  int i = 0;
  for ( ; i < n; i++)
  { int self = *col[i];
    char* y = (*fe)(self);
    result[i] = y;
  }
  result[n] = NULL;
  return result;
}

void** collectlong(long* col[], void* (*fe)(long))
{ int n = length((void**) col);
  void** result = (void**) calloc(n+1, sizeof(void*));
  int i = 0;
  for ( ; i < n; i++)
  { long self = *col[i];
    result[i] = (*fe)(self);
  }
  result[n] = NULL;
  return result;
}

void** collectdouble(double* col[], void* (*fe)(double))
{ int n = length((void**) col);
  void** result = (void**) calloc(n+1, sizeof(void*));
  int i = 0;
  for ( ; i < n; i++)
  { double self = *col[i];
    result[i] = (*fe)(self);
  }
  result[n] = NULL;
  return result;
}


void* iterate(void* col[], void* init, void* (*f)(void*,void*))
{ /* apply f repeatedly for each element of col */ 
  void* res = init; 
  int n = length(col); 
  int i = 0; 
  for ( ; i < n; i++) 
  { res = f(col[i],res); } 
  return res; 
} 

char** unionString(char* col1[], char* col2[])
{ int n = length((void**) col1);
  int m = length((void**) col2);
  char** result = (char**) calloc(n + m + 1, sizeof(char*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { char* ex = col1[i];
    if (isInString(ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  i = 0;
  for ( ; i < m; i++)
  { char* ex = col2[i];
    if (isInString(ex, result)) { }
    else  
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}


char** asSetString(char* col1[])
{ int n = length((void**) col1);
  char** result = (char**) calloc(n + 1, sizeof(char*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { char* ex = col1[i];
    if (isInString(ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  result[j] = NULL;
  return result;
}

char** asOrderedSetString(char* col[])
{ return asSetString(col); } 

int** asSetint(int* col1[])
{ int n = length((void**) col1);
  int** result = (int**) calloc(n + 1, sizeof(int*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { int* ex = col1[i];
    if (isInint(*ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  result[j] = NULL;
  return result;
}

int** asOrderedSetint(int* col[])
{ return asSetint(col); } 

long** asSetlong(long* col1[])
{ int n = length((void**) col1);
  long** result = (long**) calloc(n + 1, sizeof(long*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { long* ex = col1[i];
    if (isInlong(*ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  result[j] = NULL;
  return result;
}

long** asOrderedSetlong(long* col[])
{ return asSetlong(col); } 

double** asSetdouble(double* col1[])
{ int n = length((void**) col1);
  double** result = (double**) calloc(n + 1, sizeof(double*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { double* ex = col1[i];
    if (isIndouble(*ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  result[j] = NULL;
  return result;
}

double** asOrderedSetdouble(double* col[])
{ return asSetdouble(col); } 


unsigned char** asSetboolean(unsigned char* col1[])
{ int n = length((void**) col1);
  unsigned char** result = (unsigned char**) calloc(n + 1, sizeof(unsigned char*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { int* ex = col1[i];
    if (isInboolean(*ex, result)) { }
    else   
    { result[j] = ex; j++; }
  }   
  result[j] = NULL;
  return result;
}

unsigned char** asOrderedSetboolean(unsigned char* col[])
{ return asSetboolean(col); } 


void** unionOclAny(void* col1[], void* col2[])
{ int n = length((void**) col1);
  int m = length((void**) col2);
  void** result = (void**) calloc(n + m + 1, sizeof(void*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { void* ex = col1[i];
    if (isIn(ex, result)) 
    { }
    else   
    { result[j] = ex; 
      j++; 
    }
  }   
  i = 0;
  for ( ; i < m; i++)
  { void* ex = col2[i];
    if (isIn(ex, result)) { }
    else  
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}

void** unionAll(void** col[])
{ int totalsize = 0;
  int n = length(col);  
  int i = 0;
  for ( ; i < n; i++)
  { void** ex = col[i];
    totalsize = totalsize + length(ex); 
  }

  void** result = (void**) calloc(totalsize + 1, sizeof(void*));

  if (n == 0) 
  { return result; }  
  result = col[0]; 
  i = 1; 
  for ( ; i < n; i++)
  { void** ex = col[i];
    result = unionOclAny(result,ex); 
  }
  return result; 
} 
  

char** intersectionString(char* _col1[], char* _col2[])
{ int n = length((void**) _col1);
  int m = length((void**) _col2);
  char** result = (char**) calloc(n + 1, sizeof(char*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { char* _ex = _col1[i];
    if (isInString(_ex, _col2))
    { result[j] = _ex; j++; }
  }
  result[j] = NULL;
  return result;
}

char** concatenateString(char* _col1[], char* _col2[])
{ int n = length((void**) _col1);
  int m = length((void**) _col2);
  char** result = (char**) calloc(n + m + 1, sizeof(char*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { result[j] = _col1[i];
    j++;
  }
  i = 0;
  for ( ; i < m; i++)
  { result[j] = _col2[i];
    j++;
  }
  result[j] = NULL;
  return result;
}

int** concatenateint(int* _col1[], int* _col2[])
{ int n = length((void**) _col1);
  int m = length((void**) _col2);
  int** result = (int**) calloc(n + m + 1, sizeof(int*));
  int i = 0;
  int j = 0;
  for ( ; i < n; i++)
  { result[j] = _col1[i];
    j++;
  }
  i = 0;
  for ( ; i < m; i++)
  { result[j] = _col2[i];
    j++;
  }
  result[j] = NULL;
  return result;
}


  int** appendint(int* col[], int ex)
  { int** result;
    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (int**) calloc(len + ALLOCATIONSIZE + 1, sizeof(int*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    int* elem = (int*) malloc(sizeof(int));
    *elem = ex; 
  
    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }

  long** appendlong(long* col[], long ex)
  { long** result;
    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (long**) calloc(len + ALLOCATIONSIZE + 1, sizeof(long*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    long* elem = (long*) malloc(sizeof(long));
    *elem = ex; 
  
    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }

  double** appenddouble(double* col[], double ex)
  { double** result;
    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (double**) calloc(len + ALLOCATIONSIZE + 1, sizeof(double*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    double* elem = (double*) malloc(sizeof(double));
    *elem = ex; 
  
    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }

  char** reverseString(char** s)
  { int n = length((void**) s);
    char** result = (char**) calloc(n+1,sizeof(char*));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = NULL; 
    return result;
  }

  int** reverseint(int** s)
  { int n = length((void**) s);
    int** result = (int**) calloc(n+1,sizeof(int*));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = NULL; 
    return result;
  }

  long** reverselong(long** s)
  { int n = length((void**) s);
    long** result = (long**) calloc(n+1,sizeof(long*));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = NULL; 
    return result;
  }

  double** reversedouble(double** s)
  { int n = length((void**) s);
    double** result = (double**) calloc(n+1,sizeof(double*));
    int x = n-1;
    int i = 0;
    for ( ; i < n; i++, x--)
    { result[i] = s[x]; }
    result[n] = NULL; 
    return result;
  }

int** removeAllint(int* col1[], int* col2[])
{ int n = length((void**) col1);
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0; int j = 0;
  for ( ; i < n; i++)
  { int* ex = col1[i];
    if (isInint(*ex, col2)) {}
    else 
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}

long** removeAlllong(long* col1[], long* col2[])
{ int n = length((void**) col1);
  long** result = (long**) calloc(n+1, sizeof(long*));
  int i = 0; int j = 0;
  for ( ; i < n; i++)
  { long* ex = col1[i];
    if (isInlong(*ex, col2)) {}
    else 
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}

double** removeAlldouble(double* col1[], double* col2[])
{ int n = length((void**) col1);
  double** result = (double**) calloc(n+1, sizeof(double*));
  int i = 0; int j = 0;
  for ( ; i < n; i++)
  { double* ex = col1[i];
    if (isIndouble(*ex, col2)) {}
    else 
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}


  char** insertString(char* col[], char* ex)
  { char** result;
    if (isInString(ex, col)) 
    { return col; }

    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (char**) calloc(len + ALLOCATIONSIZE + 1, sizeof(char*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }
    result[len] = ex;
    result[len+1] = NULL;
    return result;
  }

  int** insertint(int* col[], int ex)
  { int** result;
    if (isInint(ex, col)) 
    { return col; }

    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (int**) calloc(len + ALLOCATIONSIZE + 1, sizeof(int*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    int* elem = (int*) malloc(sizeof(int));
    *elem = ex; 
    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }

  long** insertlong(long* col[], long ex)
  { long** result;
    if (isInlong(ex, col)) 
    { return col; }

    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (long**) calloc(len + ALLOCATIONSIZE + 1, sizeof(long*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    long* elem = (long*) malloc(sizeof(long));
    *elem = ex; 
  
    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }

  double** insertdouble(double* col[], double ex)
  { double** result;
    if (isIndouble(ex, col)) 
    { return col; }

    int len = length((void**) col);
    if (len % ALLOCATIONSIZE == 0)
    { result = (double**) calloc(len + ALLOCATIONSIZE + 1, sizeof(double*));
      int i = 0;
      for ( ; i < len; i++)
      { result[i] = col[i]; }
    }
    else
    { result = col; }

    double* elem = (double*) malloc(sizeof(double));
    *elem = ex; 

    result[len] = elem;
    result[len+1] = NULL;
    return result;
  }


char* copy_String(char* s)
{ char* temp = (char*) calloc(strlen(s) + 1, sizeof(char)); 
  return strcpy(temp,s); 
} 

void** copy_Sequence(void* col[])
{ int n = length(col); 
  void** result = calloc(n + 1, sizeof(void*));
  int i = 0; 
  for ( ; i < n; i++) 
  { result[i] = col[i]; } 
  result[i] = NULL; 
  return result; 
} 

void** copy_Set(void* col[])
{ return copy_Sequence(col); } 



  void** newOclAnyList(void)
  { void** result;
    result = (void**) calloc(ALLOCATIONSIZE + 1, sizeof(void*));
    result[0] = NULL;
    return result;
  }

  char** newStringList(void)
  { char** result;
    result = (char**) calloc(ALLOCATIONSIZE + 1, sizeof(char*));
    result[0] = NULL;
    return result;
  }

  int** newintList(void)
  { int** result;
    result = (int**) calloc(ALLOCATIONSIZE + 1, sizeof(int*));
    result[0] = NULL;
    return result;
  }

int** initialiseSequenceint(int sze, ...)
{ int** result;
  result = (int**) calloc(sze + 1, sizeof(int*));
  va_list ap; 
  va_start(ap,sze);
  int i = 0; 
  for ( ; i < sze; i++) 
  { int x = va_arg(ap,int);
    int* ptr = malloc(sizeof(int));
    *ptr = x; 
    result[i] = ptr; 
  } 
  result[i] = NULL; 
  return result; 
} 


  long** newlongList(void)
  { long** result;
    result = (long**) calloc(ALLOCATIONSIZE + 1, sizeof(long*));
    result[0] = NULL;
    return result;
  }

long** initialiseSequencelong(int sze, ...)
{ long** result;
  result = (long**) calloc(sze + 1, sizeof(long*));
  va_list ap; 
  va_start(ap,sze);
  int i = 0; 
  for ( ; i < sze; i++) 
  { long x = va_arg(ap,long);
    long* ptr = malloc(sizeof(long));
    *ptr = x; 
    result[i] = ptr; 
  } 
  result[i] = NULL; 
  return result; 
} 

  double** newdoubleList(void)
  { double** result;
    result = (double**) calloc(ALLOCATIONSIZE + 1, sizeof(double*));
    result[0] = NULL;
    return result;
  }

double** initialiseSequencedouble(int sze, ...)
{ double** result;
  result = (double**) calloc(sze + 1, sizeof(double*));
  va_list ap; 
  va_start(ap,sze);
  int i = 0; 
  for ( ; i < sze; i++) 
  { double x = va_arg(ap,double);
    double* ptr = malloc(sizeof(double));
    *ptr = x; 
    result[i] = ptr; 
  } 
  result[i] = NULL; 
  return result; 
} 

char** removeString(char* col[], char* ex)
{ int len = length((void**) col);
  char** result = (char**) calloc(len+1, sizeof(char*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { char* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (strcmp(eobj,ex) == 0) { }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}


char** removeFirstString(char* col[], char* ex)
{ int len = length((void**) col);
  unsigned char notfound = TRUE; 

  char** result = (char**) calloc(len+1, sizeof(char*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { char* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (strcmp(eobj,ex) == 0 && notfound) 
    { notfound = FALSE; }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

char** excludingFirstString(char* col[], char* ex)
{ return removeFirstString(col,ex); } 

char** removeAllString(char* col1[], char* col2[])
{ int n = length((void**) col1);
  char** result = (char**) calloc(n+1, sizeof(char*));
  int i = 0; int j = 0;
  for ( ; i < n; i++)
  { char* ex = col1[i];
    if (isInString(ex, col2)) {}
    else
    { result[j] = ex; j++; }
  }
  result[j] = NULL;
  return result;
}


char** removeAtString(char* col1[], int index)
{ int n = length((void**) col1);
  char** result = (char**) calloc(n+1, sizeof(char*));
  int i = 0; 
  int j = 0;
  for ( ; i < n; i++)
  { char* ex = col1[i];
    if (i+1 == index) {}
    else
    { result[j] = ex; 
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

char** excludingAtString(char* col1[], int index)
{ return removeAtString(col1,index); } 

char** subrangeString(char** col, int i, int j)
{ /* OCL indexing: 1..n */

  int len = length((void**) col);
  if (i > j || j > len) 
  { return NULL; }

  char** result = (char**) calloc(j - i + 2, sizeof(char*));
  int k = i-1;
  int l = 0;
  for ( ; k < j; k++, l++)
  { result[l] = col[k]; }
  result[l] = NULL;
  return result;
}

int** subrangeint(int** col, int i, int j)
{ /* OCL indexing: 1..n */

  int len = length((void**) col);
  if (i < 0) { i = len + i; } 
  if (j < 0) { j = len + j; } 
    
  if (i > j || j > len) 
  { return NULL; }

  int** result = (int**) calloc(j - i + 2, sizeof(int*));
  int k = i-1;
  int l = 0;
  for ( ; k < j; k++, l++)
  { result[l] = col[k]; }
  result[l] = NULL;
  return result;
}

int** removeint(int* col[], int ex)
{ int len = length((void**) col);
  int** result = (int**) calloc(len+1, sizeof(int*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { int* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == ex) { }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

int** removeFirstint(int* col[], int* ex)
{ int len = length((void**) col);
  unsigned char notfound = TRUE; 

  int** result = (int**) calloc(len+1, sizeof(int*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { int* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == *ex && notfound) 
    { notfound = FALSE; }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

int** removeAtint(int* col1[], int index)
{ int n = length((void**) col1);
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0; 
  int j = 0;
  for ( ; i < n; i++)
  { int* ex = col1[i];
    if (i+1 == index) {}
    else
    { result[j] = ex; 
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

char** frontString(char* col[])
{ int n = length((void**) col);
  return subrangeString(col, 1, n-1); 
}

char** tailString(char* col[])
{ int n = length((void**) col);
  return subrangeString(col, 2, n); 
}


int** insertIntoint(int* col1[], int ind, int* col2[])
{ if (ind <= 0) { return col1; }
  int n = length((void**) col1);
  int m = length((void**) col2);
  if (m == 0) { return col1; }

  int** result = (int**) calloc(n + m + 1, sizeof(int*));
  int i = 0; 
  int j = 0;

  for ( ; i < ind - 1 && i < n; i++)
  { result[i] = col1[i]; }

  if (i == ind - 1)
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }

    for ( ; i < n + m; i++)
    { result[i] = col1[i - m]; }
  }
  else 
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }
  }

  result[n+m] = NULL;
  return result;
}

long** insertIntolong(long* col1[], int ind, long* col2[])
{ if (ind <= 0) { return col1; }
  int n = length((void**) col1);
  int m = length((void**) col2);
  if (m == 0) { return col1; }

  long** result = (long**) calloc(n + m + 1, sizeof(long*));
  int i = 0; 
  int j = 0;

  for ( ; i < ind - 1 && i < n; i++)
  { result[i] = col1[i]; }

  if (i == ind - 1)
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }

    for ( ; i < n + m; i++)
    { result[i] = col1[i - m]; }
  }
  else 
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }
  }

  result[n+m] = NULL;
  return result;
}

double** insertIntodouble(double* col1[], int ind, double* col2[])
{ if (ind <= 0) { return col1; }
  int n = length((void**) col1);
  int m = length((void**) col2);
  if (m == 0) { return col1; }

  double** result = (double**) calloc(n + m + 1, sizeof(double*));
  int i = 0; 
  int j = 0;

  for ( ; i < ind - 1 && i < n; i++)
  { result[i] = col1[i]; }

  if (i == ind - 1)
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }

    for ( ; i < n + m; i++)
    { result[i] = col1[i - m]; }
  }
  else 
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }
  }

  result[n+m] = NULL;
  return result;
}

char** insertAtString(char* col1[], int ind, char* x)
{ if (ind <= 0) { return col1; }
  int n = length((void**) col1);
  
  char** result = (char**) calloc(n + 2, sizeof(char*));
  int i = 0; 
  
  for ( ; i < ind - 1 && i < n; i++)
  { result[i] = col1[i]; }

  if (i == ind - 1)
  { result[i] = x; 
    i++; 

    for ( ; i < n + 1; i++)
    { result[i] = col1[i - 1]; }
  }
  else 
  { result[i] = x; 
    i++; 
  }
  
  result[n+1] = NULL;
  return result;
}

char** insertIntoString(char* col1[], int ind, char* col2[])
{ if (ind <= 0) { return col1; }
  int n = length((void**) col1);
  int m = length((void**) col2);
  if (m == 0) { return col1; }

  char** result = (char**) calloc(n + m + 1, sizeof(char*));
  int i = 0; 
  int j = 0;

  for ( ; i < ind - 1 && i < n; i++)
  { result[i] = col1[i]; }

  if (i == ind - 1)
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }

    for ( ; i < n + m; i++)
    { result[i] = col1[i - m]; }
  }
  else 
  { for ( ; j < m; j++, i++)
    { result[i] = col2[j]; }
  }

  result[n+m] = NULL;
  return result;
}

long** removelong(long* col[], long ex)
{ int len = length((void**) col);
  long** result = (long**) calloc(len+1, sizeof(long*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { long* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == ex) { }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

long** removeAtlong(long* col1[], int index)
{ int n = length((void**) col1);
  long** result = (long**) calloc(n+1, sizeof(long*));
  int i = 0; 
  int j = 0;
  for ( ; i < n; i++)
  { long* ex = col1[i];
    if (i+1 == index) {}
    else
    { result[j] = ex; 
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

long** removeFirstlong(long* col[], int* ex)
{ int len = length((void**) col);
  unsigned char notfound = TRUE; 

  long** result = (long**) calloc(len+1, sizeof(long*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { long* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == *ex && notfound) 
    { notfound = FALSE; }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

double** removedouble(double* col[], double ex)
{ int len = length((void**) col);
  double** result = (double**) calloc(len+1, sizeof(double*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { double* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == ex) { }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

double** removeAtdouble(double* col1[], int index)
{ int n = length((void**) col1);
  double** result = (double**) calloc(n+1, sizeof(double*));
  int i = 0; 
  int j = 0;
  for ( ; i < n; i++)
  { double* ex = col1[i];
    if (i+1 == index) {}
    else
    { result[j] = ex; 
      j++; 
    }
  }
  result[j] = NULL;
  return result;
}

double** removeFirstdouble(double* col[], int* ex)
{ int len = length((void**) col);
  unsigned char notfound = TRUE; 

  double** result = (double**) calloc(len+1, sizeof(double*));
  int j = 0;
  int i = 0;
  for ( ; i < len; i++)
  { double* eobj = col[i];
    if (eobj == NULL)
    { result[j] = NULL;
      return result; 
    }

    if (*eobj == *ex && notfound) 
    { notfound = FALSE; }
    else
    { result[j] = eobj; j++; }
  }
  result[j] = NULL;
  return result;
}

void displayString(char* s)
{ printf("%s\n", s); }


void displayint(int s)
{ printf("%d\n", s); }

void displaylong(long s)
{ printf("%ld\n", s); }

void displaydouble(double s)
{ printf("%f\n", s); }

void displayboolean(unsigned char s)
{ if (s == TRUE) 
  { printf("%s\n", "true"); }
  else
  { printf("%s\n", "false"); }
}

void displaySequence(char** ss)
{ int x = 0; 
  printf("Sequence{ "); 
  int len = length((void**) ss);
  for ( ; x < len - 1; x++) 
  { printf("%s, ", ss[x]); }
  if (x < len)
  { printf("%s", ss[x]); }
  printf(" }\n");  
} 

void displaySet(char** ss)
{ int x = 0; 
  printf("Set{ "); 
  int len = length((void**) ss);
  for ( ; x < len - 1; x++) 
  { printf("%s, ", ss[x]); }
  if (x < len)
  { printf("%s", ss[x]); }
  printf(" }\n");  
} 


char* toString_String(void* self)
{ return (char*) self; } 

char* toString_int(void* self)
{ int* p = (int*) self; 
  int x = *p; 
  int y = x; 
  if (x < 0) { y = -x; } 
  int xwidth = (int) ceil(log10(y+1) + 2); 
  char* s = (char*) calloc(xwidth, sizeof(char)); 
  sprintf(s, "%d", x); 
  return s; 
} 

char* intToString(int x)
{ int y = x; 
  if (x < 0) { y = -x; } 
  int xwidth = (int) ceil(log10(y+1) + 2); 
  char* s = (char*) calloc(xwidth, sizeof(char)); 
  sprintf(s, "%d", x); 
  return s; 
} 

char* toString_long(void* self)
{ long* p = (long*) self; 
  long x = *p; 
  long y = x; 
  if (x < 0) { y = -x; } 
  int xwidth = (int) ceil(log10((double) y+1) + 2); 
  char* s = (char*) calloc(xwidth, sizeof(char)); 
  sprintf(s, "%ld", x); 
  return s; 
} 

char* longToString(long x)
{ long y = x; 
  if (x < 0) { y = -x; } 
  int xwidth = (int) ceil(log10((double) y+1) + 2); 
  char* s = (char*) calloc(xwidth, sizeof(char)); 
  sprintf(s, "%ld", x); 
  return s; 
} 

char* toString_double(void* self)
{ double* p = (double*) self; 
  double x = *p; 
  char* s = (char*) calloc(26, sizeof(char)); 
  sprintf(s, "%lf", x); 
  return s; 
} 

char* doubleToString(double x)
{ char* s = (char*) calloc(26, sizeof(char)); 
  sprintf(s, "%lf", x); 
  return s; 
} 

char* toString_boolean(void* self)
{ unsigned char* p = (unsigned char*) self; 
  unsigned char x = *p; 
  if (x == TRUE)  
  { return "true"; }  
  return "false"; 
} 

char* booleanToString(unsigned char x)
{ if (x == TRUE)  
  { return "true"; }  
  return "false"; 
} 

char* OclAnyToString(void* x)
{ char* ss = (char*) x;
  int ind = 0;

  int* ix = (int*) x;
  long* lx = (long*) x; 
  double* dx = (double*) x; 

  char* sbufi = (char*) calloc(21, sizeof(char)); 
  char* sbufl = (char*) calloc(21, sizeof(char)); 
  char* sbufd = (char*) calloc(21, sizeof(char)); 

  if (ss[0] == '\0')
  { sprintf(sbufi, "%d", *ix);
    sprintf(sbufd, "%f", *dx);
    if (strcmp(sbufi,"0") == 0 && 
        strcmp(sbufd,"0.000000") == 0)
    { return "0"; }
    else if (strcmp(sbufd,"0.000000") != 0)
    { return sbufd; }
    else 
    { return sbufi; }
  }
 
  while (isprint(ss[ind]) && 0 < ss[ind] && ss[ind] < 128 && ind < 1024)
  { /* printf("%d\n", ss[ind]); */   
    ind++; 
  }

  if (ss[ind] == '\0')
  { return (char*) x; } 
  
  sprintf(sbufi, "%d", *ix);
  sprintf(sbufl, "%ld", *lx);
  sprintf(sbufd, "%f", *dx);
    
  if (strcmp(sbufd,"0.000000") != 0)
  { free(sbufi); 
    free(sbufl); 
      
    return sbufd;
  } 

  if (strcmp(sbufi,sbufl) == 0)
  { free(sbufd); 
    free(sbufl); 
    return sbufi; 
  }
  else 
  { free(sbufd); 
    free(sbufi); 
    return sbufl; 
  }
}

char* StringCollectionToString(char** sq)
{ char* res = "Collection{"; 
  int n = length((void**) sq); 
  int i = 0; 
  for ( ; i < n; i++) 
  { res = concatenateStrings(res,sq[i]); 
    if (i < n-1)
    { res = concatenateStrings(res,", "); }
  }  
  return concatenateStrings(res,"}"); 
} 

char* intCollectionToString(char** sq)
{ char* res = "Collection{"; 
  int n = length((void**) sq); 
  int i = 0; 
  for ( ; i < n; i++) 
  { res = concatenateStrings(res,intToString(*sq[i])); 
    if (i < n-1)
    { res = concatenateStrings(res,", "); }
  }  
  return concatenateStrings(res,"}"); 
} 




unsigned char isInteger(char* s)
{ int x = 0;
  if (sscanf(s, "%d", &x) == 1)
  { char* ss = toString_int(&x); 
    if (strcmp(ss,s) == 0) { return TRUE; }
  } 
  return FALSE;
}

unsigned char isLong(char* s)
{ long x = 0;
  if (sscanf(s, "%ld", &x) == 1)
  { char* ss = toString_long(&x); 
    if (strcmp(ss,s) == 0) { return TRUE; }
  } 
  return FALSE;
}

unsigned char isDouble(char* s)
{ char* tmp = (char*) calloc(26, sizeof(char)); 
  double x = strtod(s, &tmp);
  if (strlen(tmp) > 0)
  { free(tmp); return FALSE; }
  free(tmp); 
  /* printf("%lf\n", x); */   
  return TRUE;
}


int* intSubrange(int a, int b)
{ if (b < a) { return NULL; }
  int n = b-a+1;
  int* result = (int*) calloc(n, sizeof(int));
  int i = 0;
  for ( ; i < n; i++)
  { result[i] = a+i; }
  return result;
}

int** integerSubrange(int a, int b)
{ if (b < a) { return NULL; }
  int n = b-a+1;
  int** result = (int**) calloc(n+1, sizeof(int*));
  int i = 0;
  for ( ; i < n; i++)
  { int* y = (int*) malloc(sizeof(int));
    *y = a+i; 
    result[i] = y;
  }
  result[n] = NULL; 
  return result;
}

  int sumint(int col[], int n)
  { int result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { result += col[i]; }
    return result;
  }

  int prdint(int col[], int n)
  { int result = 1;
    int i = 0;
    for ( ; i < n; i++)
    { result *= col[i]; }
    return result;
  }

  int sum_int(int* col[])
  { int result = 0;
    int n = length((void**) col); 
    int i = 0;
    for ( ; i < n; i++)
    { result += *col[i]; }
    return result;
  }

  int prd_int(int* col[])
  { int result = 1;
    int n = length((void**) col); 
    
    int i = 0;
    for ( ; i < n; i++)
    { result *= *col[i]; }
    return result;
  }

  long sumlong(long col[], int n)
  { long result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { result += col[i]; }
    return result;
  }

  long prdlong(long col[], int n)
  { long result = 1;
    int i = 0;
    for ( ; i < n; i++)
    { result *= col[i]; }
    return result;
  }

  long sum_long(long* col[])
  { long result = 0;
    int n = length((void**) col); 
    int i = 0;
    for ( ; i < n; i++)
    { result += *col[i]; }
    return result;
  }

  long prd_long(long* col[])
  { long result = 1;
    int n = length((void**) col); 
    int i = 0;
    for ( ; i < n; i++)
    { result *= *col[i]; }
    return result;
  }

  double sumdouble(double col[], int n)
  { double result = 0;
    int i = 0;
    for ( ; i < n; i++)
    { result += col[i]; }
    return result;
  }

  double prddouble(double col[], int n)
  { double result = 1;
    int i = 0;
    for ( ; i < n; i++)
    { result *= col[i]; }
    return result;
  }

  double sum_double(double* col[])
  { double result = 0.0;
    int n = length((void**) col); 
    int i = 0;
    for ( ; i < n; i++)
    { result += *col[i]; }
    return result;
  }

  double prd_double(double* col[])
  { double result = 1.0;
    int n = length((void**) col); 
    
    int i = 0;
    for ( ; i < n; i++)
    { result *= *col[i]; }
    return result;
  }


  int intSum(int a, int b, int (*fe)(int))
  { int result = 0;
    int i = a;
    for ( ; i <= b; i++)
    { result += (*fe)(i); }
    return result;
  }

  int intPrd(int a, int b, int (*fe)(int))
  { int result = 1;
    int i = a;
    for ( ; i <= b; i++)
    { result *= (*fe)(i); }
    return result;
  }

  long longSum(int a, int b, long (*fe)(int))
  { long result = 0;
    int i = a;
    for ( ; i <= b; i++)
    { result += (*fe)(i); }
    return result;
  }

  long longPrd(int a, int b, long (*fe)(int))
  { long result = 1;
    int i = a;
    for ( ; i <= b; i++)
    { result *= (*fe)(i); }
    return result;
  }

  double doubleSum(int a, int b, double (*fe)(int))
  { double result = 0;
    int i = a;
    for ( ; i <= b; i++)
    { result += (*fe)(i); }
    return result;
  }

  double doublePrd(int a, int b, double (*fe)(int))
  { double result = 1;
    int i = a;
    for ( ; i <= b; i++)
    { result *= (*fe)(i); }
    return result;
  }

 /* max/min operations assume col.size > 0 */ 

  double maxdouble(double col[], int n)
  { int i = 1;
    double result = col[0];
    for ( ; i < n; i++)
    { if (col[i] > result)
      { result = col[i]; }
    }
    return result;
  }

  double max_double(double* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return -HUGE_VAL; } 

    double result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] > result)
      { result = *col[i]; }
    }
    return result;
  }

  double mindouble(double col[], int n)
  { int i = 1;
    double result = col[0];
    for ( ; i < n; i++)
    { if (col[i] < result)
      { result = col[i]; }
    }
    return result;
  }

  double min_double(double* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return HUGE_VAL; } 

    double result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] < result)
      { result = *col[i]; }
    }
    return result;
  }

  int maxint(int col[], int n)
  { int i = 1;
    int result = col[0];
    for ( ; i < n; i++)
    { if (col[i] > result)
      { result = col[i]; }
    }
    return result;
  }

  int max_int(int* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return INT_MIN; } 

    int result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] > result)
      { result = *col[i]; }
    }
    return result;
  }


  int minint(int col[], int n)
  { int i = 1;
    int result = col[0];
    for ( ; i < n; i++)
    { if (col[i] < result)
      { result = col[i]; }
    }
    return result;
  }

  int min_int(int* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return INT_MAX; } 

    int result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] < result)
      { result = *col[i]; }
    }
    return result;
  }

  long maxlong(long col[], int n)
  { int i = 1;
    long result = col[0];
    for ( ; i < n; i++)
    { if (col[i] > result)
      { result = col[i]; }
    }
    return result;
  }

  long max_long(long* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return LONG_MIN; } 

    long result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] > result)
      { result = *col[i]; }
    }
    return result;
  }

  long minlong(long col[], int n)
  { int i = 1;
    long result = col[0];
    for ( ; i < n; i++)
    { if (col[i] < result)
      { result = col[i]; }
    }
    return result;
  }

  long min_long(long* col[])
  { int i = 1;
    int n = length((void**) col); 

    if (n == 0) 
    { return LONG_MAX; } 

    long result = *col[0];
    for ( ; i < n; i++)
    { if (*col[i] < result)
      { result = *col[i]; }
    }
    return result;
  }


  char* sumString(char* col[], int len)
  { int n = 0;
    int i = 0;
    for ( ; i < len; i++)
    { n += strlen(col[i]); }
    char* result = (char*) calloc(n+1, sizeof(char));
    i = 0;
    for ( ; i < len; i++)
    { result = strcat(result, col[i]); }
    result[n] = '\0';
    return result;
  }

  char* sum_String(char* col[])
  { int n = 0;
    int len = length((void**) col);
    int i = 0;
    for ( ; i < len; i++)
    { n += strlen(col[i]); }
    char* result = (char*) calloc(n+1, sizeof(char));
    i = 0;
    for ( ; i < len; i++)
    { result = strcat(result, col[i]); }
    result[n] = '\0';
    return result;
  }

   char* max_String(char* col[])
   { int n = length((void**) col);
     if (n == 0) { return NULL; }
     char* result = col[0];
     int i = 0;
     for ( ; i < n; i++)
     { if (strcmp(result, col[i]) < 0)
       { result = col[i]; }
     }
     return result;
   }

   char* min_String(char* col[])
   { int n = length((void**) col);
     if (n == 0) { return NULL; }
     char* result = col[0];
     int i = 0;
     for ( ; i < n; i++)
     { if (strcmp(result, col[i]) > 0)
       { result = col[i]; }
     }
     return result;
   }

  char* insertAtStrings(char* st1, int ind, char* st2)
  { if (ind <= 0) { return st1; }
    int n = strlen(st1);
    int m = strlen(st2);
    if (m == 0) { return st1; }
    if (ind > n) { ind = n+1; } 

    char* result = (char*) calloc(n + m + 1, sizeof(char));
    int i = 0;
    for ( ; i < ind - 1; i++)
    { result[i] = st1[i]; }
    if (i == ind - 1)
    { int j = 0;
      for ( ; j < m; j++, i++)
      { result[i] = st2[j]; }
      for ( ; i < n + m; i++)
      { result[i] = st1[i - m]; }
    }
    else
    { int j = 0;
      for ( ; j < m; j++, i++)
      { result[i] = st2[j]; }
    }
    result[n+m] = '\0';
    return result;
  }

  char* insertIntoStrings(char* st1, int ind, char* st2)
  { return insertAtStrings(st1,ind,st2); } 


  char* removeAtStrings(char* st1, int ind)
  { /* OCL indexing 1..st1.size */
    if (ind <= 0) 
    { return st1; }

    int n = strlen(st1);
    if (ind > n) 
    { return st1; } 

    char* result = (char*) calloc(n + 1, sizeof(char));
    int i = 0;
    for ( ; i < ind - 1; i++)
    { result[i] = st1[i]; }
    
    for ( ; i < n-1; i++)
    { result[i] = st1[i + 1]; }
   
    result[i] = '\0';
    return result;
  }

  char* excludingAtStrings(char* st1, int ind)
  { return removeAtStrings(st1,ind); } 

void** setAtSequence(void* col[], int ind, void* x)
{ if (ind <= 0) 
  { return col; } 

  int n = length(col); 

  if (ind > n)
  { return col; } 

  col[ind-1] = x; 
  return col; 
} 

int** setAtSequenceint(int* col[], int ind, int x)
{ if (ind <= 0) 
  { return col; } 

  int n = length((void**) col); 

  if (ind > n)
  { return col; } 

  int* ptr = malloc(sizeof(int)); 
  *ptr = x; 
  col[ind-1] = ptr; 
  return col; 
} 

long** setAtSequencelong(long* col[], int ind, long x)
{ if (ind <= 0) 
  { return col; } 

  int n = length((void**) col); 

  if (ind > n)
  { return col; } 

  long* ptr = malloc(sizeof(long)); 
  *ptr = x; 
  col[ind-1] = ptr; 
  return col; 
} 

double** setAtSequencedouble(double* col[], int ind, double x)
{ if (ind <= 0) 
  { return col; } 

  int n = length((void**) col); 

  if (ind > n)
  { return col; } 

  double* ptr = malloc(sizeof(double)); 
  *ptr = x; 
  col[ind-1] = ptr; 
  return col; 
} 

unsigned char** setAtSequenceboolean(unsigned char* col[], int ind, unsigned char x)
{ if (ind <= 0) 
  { return col; } 

  int n = length((void**) col); 

  if (ind > n)
  { return col; } 

  unsigned char* ptr = malloc(sizeof(unsigned char)); 
  *ptr = x; 
  col[ind-1] = ptr; 
  return col; 
} 

  char* setAtStrings(char* st1, int ind, char* val)
  { /* OCL indexing 1..st1.size */
    if (ind <= 0) 
    { return st1; }

    int n = strlen(st1);
    if (ind > n) 
    { return st1; } 

    char* result = (char*) calloc(n + 1, sizeof(char));
    int i = 0;
    for ( ; i < ind - 1; i++)
    { result[i] = st1[i]; }
    result[ind-1] = val[0];
    i = ind;
    for ( ; i < n; i++)
    { result[i] = st1[i]; }
   
    result[i] = '\0';
    return result;
  }

 int compareTo_String(const void* s1, const void* s2)
 { char* c1 = (char*) s1;
   char* c2 = (char*) s2;
   return strcmp(c1,c2);
 }

 int compareToString(char* c1, char* c2)
 { return strcmp(c1,c2); }


int compareTo_int(const void* s1, const void* s2)
{ int* n1 = (int*) s1;
  int* n2 = (int*) s2;
  return *n1 - *n2;
}

int compareToint(int n1, int n2)
{ if (n1 < n2) { return -1; }
  if (n1 == n2) { return 0; }
  return 1;
}

int compareTo_long(const void* s1, const void* s2)
{ long* n1 = (long*) s1;
  long* n2 = (long*) s2;
  if (*n1 < *n2) { return -1; }
  if (*n1 == *n2) { return 0; }
  return 1;
}

int compareTolong(long n1, long n2)
{ if (n1 < n2) { return -1; }
  if (n1 == n2) { return 0; }
  return 1;
}

int compareTolonglong(long long n1, long long n2)
{ if (n1 < n2) { return -1; }
  if (n1 == n2) { return 0; }
  return 1;
}

int compareTo_double(const void* s1, const void* s2)
{ double* n1 = (double*) s1;
  double* n2 = (double*) s2;
  if (*n1 < *n2) { return -1; }
  if (*n1 == *n2) { return 0; }
  return 1;
}

int compareTodouble(double n1, double n2)
{ if (n1 < n2) { return -1; }
  if (n1 == n2) { return 0; }
  return 1;
}

int compareTo_boolean(const void* s1, const void* s2)
{ unsigned char* n1 = (unsigned char*) s1;
  unsigned char* n2 = (unsigned char*) s2;
  return (int) (*n1 - *n2);
}

int compareTo_OclAny(const void* s1, const void* s2)
{ if (s1 < s2) 
  { return -1; } 
  if (s2 < s1) 
  { return 1; } 
  return 0;
}



int toInteger(char* s)
{ return (int) strtol(s, (char**) NULL, 0); } 

long toLong(char* s)
{ return strtol(s, (char**) NULL, 0); } 

unsigned char toBoolean(char* s)
{ if (strcmp("true",s) == 0)
  { return TRUE; }
  return FALSE;
}

/* The following implements BSTs for OCL sets, bags and maps */ 

struct ocltnode
{ void* object;
  struct ocltnode* left;
  struct ocltnode* right;
};

void printTree(struct ocltnode* self, char* (*tostr)(void*), int indent)
{ if (self != NULL)
  { printTree(self->left, tostr, indent+1);
    int i = 0; 
    for ( ; i < indent; i++) 
    { printf(" "); } 
    printf("%s\n", (*tostr)(self->object));
    printTree(self->right, tostr, indent+1);
  }
}

int copyTree(struct ocltnode* self, int i, void** res)
{ if (self != NULL)
  { int j = copyTree(self->left, i, res);
    res[j] = self->object;
    j++;
    return copyTree(self->right, j, res);
  }
  return i;
}


/* sorted insertion for bags */ 

struct ocltnode* addToTree(struct ocltnode* self, void* elem, int (*comp)(void*, void*))
{ if (self == NULL)
  { self = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    self->object = elem;
    self->left = NULL;
    self->right = NULL;
  }
  else
  { int cond = (*comp)(elem, self->object);
    if (cond < 0)
    { self->left = addToTree(self->left, elem, comp); }
    else
    { self->right = addToTree(self->right, elem, comp); }
  }
  return self;
}


/* sorted insertion for sets */ 

struct ocltnode* insertIntoTree(struct ocltnode* self, void* elem, int (*comp)(void*, void*))
{ if (self == NULL)
  { self = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    self->object = elem;
    self->left = NULL;
    self->right = NULL;
  }
  else
  { int cond = (*comp)(elem, self->object);
    if (cond < 0)
    { self->left = insertIntoTree(self->left, elem, comp); }
    else if (cond > 0)
    { self->right = insertIntoTree(self->right, elem, comp); }
  }
  return self;
}


/* Binary search */

void* lookupInTree(struct ocltnode* self, void* elem, int (*comp)(void*, void*))
{ if (self == NULL)
  { return NULL; }
  else
  { int cond = (*comp)(elem, self->object);
    if (cond == 0) 
    { return self->object; } 
    else if (cond < 0)
    { return lookupInTree(self->left, elem, comp); }
    else
    { return lookupInTree(self->right, elem, comp); }
  }
}



void** treesort(void* col[], int (*comp)(void*, void*))
{ if (col == NULL) { return NULL; }
  int len = length(col);
  void** res;
  res = (void**) calloc(len+1, sizeof(void*));
  struct ocltnode* tree = NULL;
  int i = 0;
  for ( ; i < len; i++)
  { tree = addToTree(tree, col[i], comp); }

  copyTree(tree, 0, res);
  res[len] = NULL;
  return res;
}


/* Max and Min for sorted sets or bags */

void* oclMax(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->right == NULL)
  { return self->object; }
  return oclMax(self->right);
}

void* oclMin(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->left == NULL)
  { return self->object; }
  return oclMin(self->left);
}

void* oclLast(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->right == NULL)
  { return self->object; }
  return oclLast(self->right);
}

void* oclFirst(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->left == NULL)
  { return self->object; }
  return oclFirst(self->left);
}


/* General-purpose self->includes(x) for any tree collection */

unsigned char oclIncludes(struct ocltnode* self, void* x)
{ if (self == NULL) { return FALSE; }
  if (self->object == x) { return TRUE; }
  if (oclIncludes(self->left, x)) { return TRUE; }
  return oclIncludes(self->right, x);
}

unsigned char oclExcludes(struct ocltnode* self, void* x)
{ if (self == NULL) { return TRUE; }
  if (self->object == x) { return FALSE; }
  if (oclExcludes(self->left, x)) { }
  else { return FALSE; }
  return oclExcludes(self->right, x);
}

unsigned char oclIncludesAll(struct ocltnode* t1, struct ocltnode* t2)
{ if (t2 == NULL)
  { return TRUE; }
  if (t1 == NULL)
  { return FALSE; }
  unsigned char b1 = oclIncludesAll(t1, t2->left);
  if (b1 == FALSE) { return FALSE; }
  if (oclIncludes(t1, t2->object)) {}
  else { return FALSE; }
  return oclIncludesAll(t1, t2->right);  
}

unsigned char oclExcludesAll(struct ocltnode* t1, struct ocltnode* t2)
{ if (t2 == NULL || t1 == NULL)
  { return TRUE; }
  if (oclIncludes(t1, t2->object)) { return FALSE; }
  if (oclExcludesAll(t1, t2->left))
  { return oclExcludesAll(t1, t2->right); }
  return FALSE; 
}

struct ocltnode* oclFront(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->right == NULL)
  { return self->left; }
  struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  newnode->object = self->object;
  newnode->left = self->left;
  newnode->right = oclFront(self->right);
  return newnode;
}

struct ocltnode* oclTail(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  if (self->left == NULL)
  { return self->right; }
  struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  newnode->object = self->object;
  newnode->left = oclTail(self->left);
  newnode->right = self->right;
  return newnode;
}

int oclSize(struct ocltnode* self)
{ if (self == NULL)
  { return 0; }
  int n1 = oclSize(self->left);
  int n2 = oclSize(self->right);
  return n1 + 1 + n2;
}

struct ocltnode* oclReverse(struct ocltnode* self)
{ if (self == NULL)
  { return NULL; }
  struct ocltnode* res = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  res->object = self->object;
  res->left = oclReverse(self->right);
  res->right = oclReverse(self->left);
  return res;
}

struct ocltnode* oclSelect(struct ocltnode* self, unsigned char (*f)(void*))
{ if (self == NULL)
  { return NULL; }

  struct ocltnode* resl = oclSelect(self->left, f);
  struct ocltnode* resr = oclSelect(self->right, f);

  if ((*f)(self->object))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = self->object;
    newnode->left = resl;
    newnode->right = resr; 
    return newnode;
  }
  else if (resr != NULL)
  { if (resl == NULL)
    { return resr; }
    else
    { struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
      newnode2->object = oclFirst(resr);
      newnode2->left = resl;
      newnode2->right = oclTail(resr);
      return newnode2;
    }
  }
  else 
  { return resl; }
}

struct ocltnode* oclReject(struct ocltnode* self, unsigned char (*f)(void*))
{ if (self == NULL)
  { return NULL; }

  struct ocltnode* resl = oclReject(self->left, f);
  struct ocltnode* resr = oclReject(self->right, f);

  if (!((*f)(self->object)))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = self->object;
    newnode->left = resl;
    newnode->right = resr; 
    return newnode;
  }
  else 
  { if (resl == NULL) { return resr; }
    if (resr == NULL) { return resl; } 
    struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode2->object = oclFirst(resr);
    newnode2->left = resl;
    newnode2->right = oclTail(resr);
    return newnode2;
  }
}

struct ocltnode* oclCollect(struct ocltnode* self, void* (*f)(void*))
{ if (self == NULL)
  { return NULL; }
  struct ocltnode* res = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  res->object = (*f)(self->object);
  res->left = oclCollect(self->left, f);
  res->right = oclCollect(self->right, f);
  return res;
}

unsigned char oclForAll(struct ocltnode* self, unsigned char (*f)(void*))
{ unsigned char result = TRUE;
  if (self == NULL)
  { return result; }
  if (oclForAll(self->left, f)) {}
  else { return FALSE; }
  if ((*f)(self->object)) {}
  else { return FALSE; }
  return oclForAll(self->right, f);
}

unsigned char oclExists(struct ocltnode* self, unsigned char (*f)(void*))
{ unsigned char result = FALSE;
  if (self == NULL)
  { return result; }
  if (oclExists(self->left, f))
  { return TRUE; }
  if ((*f)(self->object)) 
  { return TRUE; }
  return oclExists(self->right, f);
}

unsigned char oclExists1(struct ocltnode* self, unsigned char (*f)(void*))
{ struct ocltnode* res = oclSelect(self, f);
  int n = oclSize(res);
  if (n == 1) { return TRUE; }
  return FALSE;
}


/* For sets sorted using the default order on objects: */ 

struct ocltnode* oclIncluding(struct ocltnode* self, void* x)
{ struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  newnode->object = x;
  newnode->left = NULL;
  newnode->right = NULL;

  if (self == NULL)
  { return newnode; }
  if (self->object == x)
  { newnode->left = self->left; 
    newnode->right = self->right; 
    return newnode;
  }
  if ((long) self->object < (long) x)
  { newnode->object = self->object;
    newnode->left = oclIncluding(self->left, x);
    newnode->right = self->right; 
    return newnode; 
  }
  else 
  { newnode->object = self->object;
    newnode->left = self->left;
    newnode->right = oclIncluding(self->right, x); 
    return newnode; 
  }
}


/* For unsorted bags: */ 

struct ocltnode* oclIncludingBag(struct ocltnode* self, void* x)
{ struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  newnode->object = x;
  newnode->left = NULL;
  newnode->right = NULL;

  if (self == NULL)
  { return newnode; }
  if (self->left == NULL)
  { self->left = newnode;
    return self;
  }
  if (self->right == NULL)
  { self->right = newnode;
    return self;
  }
  newnode->object = self->object;
  newnode->right = self->right; 
  newnode->left = oclIncludingBag(self->left, x);
  return newnode;
}


/* For unsorted sets: */ 

struct ocltnode* oclIncludingSet(struct ocltnode* self, void* x)
{ struct ocltnode* newnode;
  if (self == NULL)
  { newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = x;
    newnode->left = NULL;
    newnode->right = NULL;
    return newnode;
  }
  if (self->object == x)
  { return self; }
  if (self->left == NULL)
  { self->left = oclIncludingSet(NULL, x);
    return self;
  }
  if (self->right == NULL)
  { self->right = oclIncludingSet(NULL, x);
    return self;
  }
  self->left = oclIncludingSet(self->left, x);
  return self;
}

struct ocltnode* oclConcatenate(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { return t2; }
  if (t2 == NULL)
  { return t1; }
  struct ocltnode* u1 = oclConcatenate(t1->right, t2);
  struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  newnode->object = t1->object;
  newnode->left = t1->left;
  newnode->right = u1;
  return newnode;
}


/* oclUnion, oclSelect, oclReject, oclExcluding, 
   oclIntersection, oclExcludesAll preserve the order 
   of the first argument in the result. */ 

struct ocltnode* oclUnion(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { return t2; }
  if (t2 == NULL)
  { return t1; }
  struct ocltnode* u1 = oclUnion(t1, t2->left);
  struct ocltnode* u2 = oclIncluding(u1, t2->object);
  return oclUnion(u2, t2->right);
}

struct ocltnode* oclUnionBag(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { return t2; }
  if (t2 == NULL)
  { return t1; }
  struct ocltnode* u1 = oclUnionBag(t1, t2->left);
  struct ocltnode* u2 = oclIncludingBag(u1, t2->object);
  return oclUnionBag(u2, t2->right);
}



struct ocltnode* oclExcluding(struct ocltnode* self, void* x)
{ if (self == NULL) { return NULL; }
  struct ocltnode* resl = oclExcluding(self->left, x);
  struct ocltnode* resr = oclExcluding(self->right, x);
  if (self->object != x)
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = self->object;
    newnode->left = resl;
    newnode->right = resr;
    return newnode;
  }
  else
  { if (resl == NULL) { return resr; }
    if (resr == NULL) { return resl; }
    struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode2->object = oclFirst(resr);
    newnode2->left = resl;
    newnode2->right = oclTail(resr);
    return newnode2;
  }
}

struct ocltnode* oclSubtract(struct ocltnode* self, struct ocltnode* col)
{ if (self == NULL) { return NULL; }
  struct ocltnode* resl = oclSubtract(self->left, col);
  struct ocltnode* resr = oclSubtract(self->right, col);
  if (oclExcludes(col, self->object))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = self->object;
    newnode->left = resl;
    newnode->right = resr;
    return newnode;
  }
  else
  { if (resl == NULL) { return resr; }
    if (resr == NULL) { return resl; }
    struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode2->object = oclFirst(resr);
    newnode2->left = resl;
    newnode2->right = oclTail(resr);
    return newnode2;
  }
}

struct ocltnode* oclIntersection(struct ocltnode* self, struct ocltnode* col)
{ if (self == NULL) { return NULL; }
  struct ocltnode* resl = oclIntersection(self->left, col);
  struct ocltnode* resr = oclIntersection(self->right, col);
  if (oclIncludes(col, self->object))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = self->object;
    newnode->left = resl;
    newnode->right = resr;
    return newnode;
  }
  else
  { if (resl == NULL) { return resr; }
    if (resr == NULL) { return resl; }
    struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode2->object = oclFirst(resr);
    newnode2->left = resl;
    newnode2->right = oclTail(resr);
    return newnode2;
  }
}


unsigned char oclEquals(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { if (t2 == NULL)
    { return TRUE; }
    return FALSE;
  }
  if (t2 == NULL)
  { if (t1 == NULL)
    { return TRUE; }
    return FALSE;
  }
  if (t1->object != t2->object)
  { return FALSE; }
  if (oclEquals(t1->left, t2->left) == TRUE)
  { return oclEquals(t1->right, t2->right); }
  return FALSE;
}


struct ocltnode* oclAsSet(void* col[])
{ if (col == NULL) { return NULL; }
  int len = length(col);
  struct ocltnode* tree = NULL;
  int i = 0;
  for ( ; i < len; i++)
  { tree = oclIncluding(tree, col[i]); }
  return tree; 
}

void** oclAsSequence(struct ocltnode* tree)
{ if (tree == NULL) { return NULL; }
  int len = oclSize(tree);
  void** res = (void**) calloc(len+1, sizeof(void*));
  
  copyTree(tree, 0, res);
  res[len] = NULL;
  return res;
}


/* Maps, used in the loading of models, for caching, and for 
   object lookup by primary key. */

struct oclmnode
{ char* key;
  void* value;
};


struct ocltnode* insertIntoMap(struct ocltnode* self, char* _key, void* _value)
{ if (self == NULL)
  { struct oclmnode* elem = (struct oclmnode*) malloc(sizeof(struct oclmnode));
    elem->key = _key;
    elem->value = _value;
    struct ocltnode* result = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    result->object = elem;
    result->left = NULL;
    result->right = NULL;
    return result;
  }
  else 
  { struct oclmnode* e = (struct oclmnode*) self->object;
    if (strcmp(_key, e->key) < 0)
    { self->left = insertIntoMap(self->left, _key, _value); }
    else if (strcmp(_key, e->key) == 0)
    { e->value = _value; }
    else
    { self->right = insertIntoMap(self->right, _key, _value); }
  }
  return self;
}

struct ocltnode* insertIntoMapint(struct ocltnode* self, char* _key, int _value)
{ int* elem = (int*) malloc(sizeof(int));
  *elem = _value; 
  return insertIntoMap(self, _key, (void*) elem); 
}

struct ocltnode* insertIntoMaplong(struct ocltnode* self, char* _key, long _value)
{ long* elem = (long*) malloc(sizeof(long));
  *elem = _value; 
  return insertIntoMap(self, _key, (void*) elem); 
}

struct ocltnode* insertIntoMapdouble(struct ocltnode* self, char* _key, double _value)
{ double* elem = (double*) malloc(sizeof(double));
  *elem = _value; 
  return insertIntoMap(self, _key, (void*) elem); 
}


void* lookupInMap(struct ocltnode* self, char* _key)
{ if (self == NULL) 
  { return NULL; }
  struct oclmnode* elem = (struct oclmnode*) self->object;

  if (strcmp(_key, elem->key) == 0)
  { return elem->value; }
  else if (strcmp(_key, elem->key) < 0)
  { return lookupInMap(self->left, _key); }
  else
  { return lookupInMap(self->right, _key); }
} 

unsigned char includesKey(struct ocltnode* m, char* _key)
{ void* value = lookupInMap(m, _key); 

  if (value == NULL)
  { return FALSE; } 
  return TRUE; 
} 

unsigned char excludesKey(struct ocltnode* m, char* _key)
{ void* value = lookupInMap(m, _key); 

  if (value == NULL)
  { return TRUE; } 
  return FALSE; 
} 

unsigned char includesValue(struct ocltnode* self, void* _val)
{ if (self == NULL) 
  { return FALSE; }

  struct oclmnode* elem = (struct oclmnode*) self->object;
  if (_val == elem->value)
  { return TRUE; }

  unsigned char inLeft = includesValue(self->left, _val); 
  if (inLeft) 
  { return TRUE; }

  return includesValue(self->right, _val); 
} 

unsigned char excludesValue(struct ocltnode* self, void* _val)
{ if (self == NULL) 
  { return TRUE; }

  struct oclmnode* elem = (struct oclmnode*) self->object;
  if (_val == elem->value)
  { return FALSE; }

  unsigned char inLeft = includesValue(self->left, _val); 
  if (inLeft) 
  { return FALSE; }

  return excludesValue(self->right, _val); 
} 

struct ocltnode* oclSelectMap(struct ocltnode* self, unsigned char (*f)(void*))
{ if (self == NULL)
  { return NULL; }

  struct ocltnode* resl = oclSelectMap(self->left, f);
  struct ocltnode* resr = oclSelectMap(self->right, f);
  struct oclmnode* node = (struct oclmnode*) self->object;

  if ((*f)(node->value))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = node;
    newnode->left = resl;
    newnode->right = resr; 
    return newnode;
  }
  else if (resr != NULL)
  { if (resl == NULL)
    { return resr; }
    else
    { struct ocltnode* newnode2 = 
        (struct ocltnode*) malloc(sizeof(struct ocltnode));
      newnode2->object = oclFirst(resr);
      newnode2->left = resl;
      newnode2->right = oclTail(resr);
      return newnode2;
    }
  }
  else 
  { return resl; }
}

struct ocltnode* oclRejectMap(struct ocltnode* self, unsigned char (*f)(void*))
{ if (self == NULL)
  { return NULL; }

  struct ocltnode* resl = oclRejectMap(self->left, f);
  struct ocltnode* resr = oclRejectMap(self->right, f);

  struct oclmnode* maplet = (struct oclmnode*) self->object;

  if (!((*f)(maplet->value)))
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode->object = maplet;
    newnode->left = resl;
    newnode->right = resr; 
    return newnode;
  }
  else 
  { if (resl == NULL) { return resr; }
    if (resr == NULL) { return resl; } 
    struct ocltnode* newnode2 = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    newnode2->object = oclFirst(resr);
    newnode2->left = resl;
    newnode2->right = oclTail(resr);
    return newnode2;
  }
}

struct ocltnode* oclCollectMap(struct ocltnode* self, void* (*f)(void*))
{ if (self == NULL)
  { return NULL; }
  struct ocltnode* res = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  struct oclmnode* maplet = (struct oclmnode*) self->object;
  struct oclmnode* emaplet = (struct oclmnode*) malloc(sizeof(struct oclmnode));
  emaplet->key = maplet->key;
  emaplet->value = (*f)(maplet->value);

  res->object = emaplet;
  res->left = oclCollectMap(self->left, f);
  res->right = oclCollectMap(self->right, f);
  return res;
}

struct ocltnode* oclComposeMaps(struct ocltnode* self, struct ocltnode* t)
{ if (self == NULL)
  { return NULL; }

  struct ocltnode* u1 = oclComposeMaps(self->left, t);
  struct ocltnode* u2 = oclComposeMaps(self->right, t);
  struct oclmnode* node = (struct oclmnode*) self->object;

  void* val = lookupInMap(t,(char*) node->value); 

  if (val != NULL)
  { struct ocltnode* newnode = (struct ocltnode*) malloc(sizeof(struct ocltnode));
    struct oclmnode* emaplet = (struct oclmnode*) malloc(sizeof(struct oclmnode));
    emaplet->key = node->key;
    emaplet->value = val;
    newnode->object = emaplet;
    newnode->left = u1;
    newnode->right = u2; 
    return newnode;
  }
  else if (u2 != NULL)
  { if (u1 == NULL)
    { return u2; }
    else
    { struct ocltnode* newnode2 = 
        (struct ocltnode*) malloc(sizeof(struct ocltnode));
      newnode2->object = oclFirst(u2);
      newnode2->left = u1;
      newnode2->right = oclTail(u2);
      return newnode2;
    }
  }
  else 
  { return u1; }
}



unsigned char oclIncludesKey(struct ocltnode* self, char* _key)
{ if (self == NULL)
  { return FALSE; }
  struct oclmnode* elem = (struct oclmnode*) self->object;

  if (strcmp(_key, elem->key) == 0)
  { return TRUE; }
  else if (strcmp(_key, elem->key) < 0)
  { return oclIncludesKey(self->left, _key); }
  else
  { return oclIncludesKey(self->right, _key); }
}

unsigned char oclIncludesValue(struct ocltnode* self, void* _value)
{ if (self == NULL)
  { return FALSE; }
  struct oclmnode* elem = (struct oclmnode*) self->object;

  if (_value == elem->value)
  { return TRUE; }
  else if (oclIncludesValue(self->left, _value))
  { return TRUE; }
  else
  { return oclIncludesValue(self->right, _value); }
}

struct ocltnode* oclIncludingMap(struct ocltnode* m, char* key, void* value)
{ return insertIntoMap(m,key,value); }
 
char** oclKeyset(struct ocltnode* m)
{ struct oclmnode** maplets = (struct oclmnode**) oclAsSequence(m);
  int n = length((void**) maplets);
  char** res = (char**) calloc(n+1, sizeof(char*));
  int x = 0;
  for ( ; x < n; x++)
  { struct oclmnode* mx = maplets[x];
    res[x] = mx->key;
  }
  res[x] = NULL;
  return res;
}

void** oclValues(struct ocltnode* m)
{ struct oclmnode** maplets = (struct oclmnode**) oclAsSequence(m);
  int n = length((void**) maplets);
  void** res = calloc(n+1, sizeof(void*));
  int x = 0;
  for ( ; x < n; x++)
  { struct oclmnode* mx = maplets[x];
    res[x] = mx->value;
  }
  res[x] = NULL;
  return res;
}

void* oclAtMap(struct ocltnode* m, char* key)
{ return lookupInMap(m,key); } 


struct ocltnode* oclUnionMap(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { return t2; }
  if (t2 == NULL)
  { return t1; }

  struct ocltnode* u1 = oclUnionMap(t1, t2->left);

  struct ocltnode* u2 = oclUnionMap(u1, t2->right);

  struct oclmnode* elem = (struct oclmnode*) t2->object;

  return insertIntoMap(u2, elem->key, elem->value);
}


struct ocltnode* unionAllMap(struct ocltnode* col[])
{ int n = length(col); 

  struct ocltnode* result = NULL; 

  if (n == 0) 
  { return result; }  

  result = col[0]; 

  int i = 1; 
  for ( ; i < n; i++)
  { struct ocltnode* ex = col[i];
    result = oclUnionMap(result,ex); 
  }
  return result; 
} 

struct ocltnode* oclIntersectionMap(struct ocltnode* t1, struct ocltnode* t2)
{ if (t1 == NULL)
  { return NULL; }
  if (t2 == NULL)
  { return NULL; }

  struct ocltnode* u1 = oclIntersectionMap(t1->left, t2);

  struct ocltnode* u2 = oclIntersectionMap(t1->right, t2);

  struct ocltnode* u = oclUnionMap(u1,u2); 

  struct oclmnode* elem = (struct oclmnode*) t1->object;
  void* v = lookupInMap(t2, elem->key); 
  if (v == NULL)
  { return u; } 
  else if (v != elem->value)
  { return u; } 
  return insertIntoMap(u, elem->key, elem->value);
}

struct ocltnode* oclRestrictMap(struct ocltnode* t, char* keys[])
{ /* the elements of t which have key in keys */ 

  if (t == NULL) 
  { return NULL; } 

  struct ocltnode* u1 = oclRestrictMap(t->left, keys);
  struct ocltnode* u2 = oclRestrictMap(t->right, keys);
  struct ocltnode* u = oclUnionMap(u1,u2); 

  struct oclmnode* elem = (struct oclmnode*) t->object;

  if (isIn(elem->key,keys))
  { return insertIntoMap(u, elem->key, elem->value); }
  return u; 
}

struct ocltnode* oclAntirestrictMap(struct ocltnode* t, char* keys[])
{ /* the elements of t which do not have key in keys */ 

  if (t == NULL) 
  { return NULL; } 

  struct ocltnode* u1 = oclAntirestrictMap(t->left, keys);
  struct ocltnode* u2 = oclAntirestrictMap(t->right, keys);
  struct ocltnode* u = oclUnionMap(u1,u2); 

  struct oclmnode* elem = (struct oclmnode*) t->object;

  if (isIn(elem->key,keys))
  { return u; } 
  return insertIntoMap(u, elem->key, elem->value); 
}

struct ocltnode* oclSubtractMap(struct ocltnode* m1, struct ocltnode* m2)
{ /* Elements of m1 whose key is not in m2->keys() */
  char** keyset2 = oclKeyset(m2); 
  struct ocltnode* result = oclAntirestrictMap(m1,keyset2); 
  return result; 
} 

void displayMap(struct ocltnode* m)
{ struct oclmnode** maplets = (struct oclmnode**) oclAsSequence(m);
  int n = length((void**) maplets);
  int x = 0;
  for ( ; x < n; x++)
  { struct oclmnode* mx = maplets[x];
    char* key = mx->key; 
    char* val = (char*) mx->value;
    printf("%s |-> %s, ", key, val); 
  }
}
 
struct ocltnode* copy_Map(struct ocltnode* t)
{ 
  if (t == NULL) 
  { return NULL; } 

  struct ocltnode* u1 = copy_Map(t->left);
  struct ocltnode* u2 = copy_Map(t->right);

  struct oclmnode* elem = (struct oclmnode*) t->object;

  struct oclmnode* newelem = (struct oclmnode*) malloc(sizeof(struct oclmnode));
  newelem->key = copy_String(elem->key);
  newelem->value = elem->value;

  struct ocltnode* result = (struct ocltnode*) malloc(sizeof(struct ocltnode));
  result->object = newelem;
  result->left = u1;
  result->right = u2;
  return result;
}

/* Utility functions to parse strings and build formats        */ 
/* isMatch, hasMatch, firstMatch, allMatches and replaceAllMatches need the 
   regex.h library */

unsigned char hasMatch(char* str, char* patt) 
{ regexp* expression = regcomp(patt); 
  int r = regexec(expression, str); 
  if (r == TRUE)
  { return TRUE; } 
  return FALSE; 
}  

unsigned char isMatch(char* str, char* patt) 
{ regexp* expression = regcomp(patt); 
  int r = regexec(expression, str); 
  if (r == TRUE)
  { if (strlen(str) == strlen(expression->startp[0]) &&
        strlen(expression->endp[0]) == 0) 
    { return TRUE; } 
  } 
  return FALSE; 
}  

char* firstMatch(char* str, char* patt)
{ int strsize = strlen(str); 
  int pattsize = strlen(patt);
 
  if (strsize == 0 || pattsize == 0)
  { return NULL; } 

  char* res = (char*) calloc(strsize, sizeof(char));

  regexp* expression = regcomp(patt); 
  int r = regexec(expression, str); 

  if (r == TRUE)
  {  
    if (expression->startp[0] != NULL && 
        expression->endp[0] != NULL)
    { char* st = expression->startp[0];
      int stlen = strlen(st);  
      char* en = expression->endp[0]; 
      int enlen = strlen(en);  
      int j = 0; 
      for ( ; j < stlen - enlen ; j++)
      { res[j] = st[j]; } 
      res[j] = '\0'; 
      return res; 
    } 
  } 
  return NULL; 
}

char** allMatches(char* str, char* patt)
{ int strsize = strlen(str); 
  int pattsize = strlen(patt);
 
  char** res = (char**) calloc(strsize + 2, sizeof(char*));

  if (strsize == 0 || pattsize == 0)
  { res[0] = str; 
    res[1] = NULL; 
    return res; 
  } 

  int i = 0; 
    
  regexp* expression = regcomp(patt); 
  int r = regexec(expression, str); 

  while (r == TRUE)
  {  
    if (expression->startp[0] != NULL && 
        expression->endp[0] != NULL)
    { char* st = expression->startp[0];
      int stlen = strlen(st);  
      char* en = expression->endp[0]; 
      int enlen = strlen(en);  
      char* mat = (char*) malloc(strsize*sizeof(char)); 
      int j = 0; 
      for ( ; j < stlen - enlen ; j++)
      { mat[j] = st[j]; } 
      mat[j] = '\0'; 
      res[i] = mat; 
      r = regexec(expression, en);
      i++;  
    }
    else 
    { r = FALSE; }
  } 
  res[i] = NULL; 
  return res; 
} 

char* replaceAllMatches(char* str, char* patt, char* rep)
{ int strsize = strlen(str); 
  int pattsize = strlen(patt); 
  int repsize = strlen(rep); 

  if (strsize == 0 || pattsize == 0) 
  { return str; } 
  if (repsize == 0) 
  { return str; }

  char** mts = allMatches(str,patt); 
  int numbermts = length((void**) mts); 

  if (numbermts == 0) 
  { return str; } 

  char* s = (char*) malloc((strsize + numbermts*repsize + 1)*sizeof(char)); 
  /* copy of s + space for all replacements. */
 
  
  int i = 0; 
  int mcount = 0;
  char* remainderOfstr = str; 
 
  for ( ; mcount < numbermts; mcount++)
  { char* mtch = mts[mcount]; 
    int k = indexOfStrings(remainderOfstr,mtch); 
    /* printf("Match %s occurs at position %d\n", mtch, k); */ 
    int j = 0; 
    for ( ; j < k-1; j++)
    { s[i] = remainderOfstr[j]; 
      i++; 
    } 
    int r = 0; 
    for ( ; r < repsize; r++) 
    { s[i] = rep[r]; 
      i++; 
    } 
    int mlen = strlen(mtch); 
    remainderOfstr = &remainderOfstr[k+mlen-1];  
  }     

  int remainderlen = strlen(remainderOfstr); 
  int t = 0; 
  for ( ; t < remainderlen; t++) 
  { s[i] = remainderOfstr[t]; 
    i++; 
  } 

  s[i] = '\0'; 
  return s; 
} 

char* replaceFirstMatch(char* str, char* patt, char* rep)
{ int strsize = strlen(str); 
  int pattsize = strlen(patt); 
  int repsize = strlen(rep); 

  if (strsize == 0 || pattsize == 0) 
  { return str; } 
  if (repsize == 0) 
  { return str; }

  char* mtch = firstMatch(str,patt); 
  if (mtch == NULL) 
  { return str; } 

  char* s = (char*) malloc((strsize + repsize + 1)*sizeof(char)); 
  /* copy of s + space for replacement. */
 
  int mlen = strlen(mtch); 
  
  int i = 0; 
  char* remainderOfstr = str; 
 
  int k = indexOfStrings(remainderOfstr,mtch); 
    /* printf("Match %s occurs at position %d\n", mtch, k); */ 
  int j = 0; 
  for ( ; j < k-1; j++)
  { s[i] = remainderOfstr[j]; 
    i++; 
  } 
  int r = 0; 
  for ( ; r < repsize; r++) 
  { s[i] = rep[r]; 
    i++; 
  } 
  
  remainderOfstr = &remainderOfstr[k+mlen-1];  
  
  int remainderlen = strlen(remainderOfstr); 
  int t = 0; 
  for ( ; t < remainderlen; t++) 
  { s[i] = remainderOfstr[t]; 
    i++; 
  } 

  s[i] = '\0'; 
  return s; 
} 

unsigned char isCSVseparator(char c)
{ if (c == ',') { return TRUE; } 
  if (c == ';') { return TRUE; } 
  return FALSE; 
} 

char** tokenise(char* str, unsigned char (*isseperator)(char)) 
{ int maxtokens = strlen(str); 
  if (maxtokens == 0) 
  { return NULL; } 

  char** tokens = (char**) calloc(maxtokens, sizeof(char*)); 
  char* token = (char*) malloc(maxtokens*sizeof(char)); 
  int resultcount = 0; 

  int i = 0;
  int j = 0; 
 
  for ( ; i < maxtokens; i++) 
  { 
    char c = str[i]; 
    if (!(*isseperator)(c))
    { token[j] = c; 
      j++; 
    } 
    else 
    { token[j] = '\0'; 
      tokens[resultcount] = token;
      resultcount++; 

      /* printf("Extracted token %s\n", token); */ 
 
      while ((*isseperator)(str[i]) && i < maxtokens)
      { i++; }
      if (i >= maxtokens) 
      { tokens[resultcount] = NULL; 
        return tokens; 
      } 
      else 
      { token = (char*) malloc(maxtokens*sizeof(char));
        token[0] = str[i]; 
        j = 1; 
      } 
    }
  } 

  token[j] = '\0'; 
  tokens[resultcount] = token; 
  /* printf("Extracted token %s\n", token); */  
  resultcount++; 
  tokens[resultcount] = NULL; 

  /* printf("Parsed %d tokens\n", resultcount); */  
  return tokens;  
} 

char** split(char* str, char* seps) 
{ int maxtokens = strlen(str); 
  if (maxtokens == 0) 
  { return NULL; } 

  char** tokens = (char**) calloc(maxtokens, sizeof(char*)); 
  char* token = (char*) malloc(maxtokens*sizeof(char)); 
  int resultcount = 0; 

  int i = 0;
  int j = 0; 
 
  for ( ; i < maxtokens; i++) 
  { 
    char c = str[i]; 
    if (!hasCharacter(seps,c))
    { token[j] = c; 
      j++; 
    } 
    else 
    { token[j] = '\0'; 
      tokens[resultcount] = token;
      resultcount++; 

      /* printf("Extracted token %s\n", token); */ 
 
      while (hasCharacter(seps,str[i]) && i < maxtokens)
      { i++; }
      if (i >= maxtokens) 
      { tokens[resultcount] = NULL; 
        return tokens; 
      } 
      else 
      { token = (char*) malloc(maxtokens*sizeof(char));
        token[0] = str[i]; 
        j = 1; 
      } 
    }
  } 

  token[j] = '\0'; 
  tokens[resultcount] = token; 
  /* printf("Extracted token %s\n", token); */  
  resultcount++; 
  tokens[resultcount] = NULL; 

  /* printf("Parsed %d tokens\n", resultcount); */  
  return tokens;  
} 




char** getFileLines(char* file)
{ FILE* f = fopen(file,"r"); 
  if (f == NULL) 
  { return NULL; }
  int MAXLINES = 100; 
  int MAXLINE = 1024; 

  char** lines = (char**) calloc(MAXLINES, sizeof(char*));
  int linecount = 0; 
 
  char* line = (char*) malloc(MAXLINE*sizeof(char));
  char* eof = fgets(line,MAXLINE,f); 
  while (eof != NULL) 
  { lines[linecount] = line; 
    linecount++; 
    /* printf("Extracted line %s", line); */ 
    line = (char*) malloc(MAXLINE*sizeof(char));
    eof = fgets(line,MAXLINE,f); 
  } 
  fclose(f);
  lines[linecount] = NULL; 
  /* printf("Extracted %d lines\n", linecount); */  
  return lines; 
}  

char* buildFormat(char** tokens) 
{ /* From a sequence of type names, builds the format string needed 
     to write/read values of these types. */

  int i = 0; 
  int tn = length(tokens);
  char* res = (char*) malloc(tn*4*sizeof(char));  
  int rind = 0; 
  res[0] = '%'; 
  res[1] = 's';
  res[2] = ' '; 
  rind = 3; 

  for ( ; i < tn-2; i++) 
  { char* tok = tokens[i];
    if (strcmp("int",tok) == 0)
    { res[rind] = '%'; 
      res[rind+1] = 'd'; 
      res[rind+2] = ' ';
      rind = rind + 3;
    } 
    else if (strcmp("double",tok) == 0)
    { res[rind] = '%'; 
      res[rind+1] = 'l'; 
      res[rind+2] = 'f';
      res[rind+3] = ' ';
      rind = rind + 4;
    } 
    else if (strcmp("long",tok) == 0)
    { res[rind] = '%'; 
      res[rind+1] = 'l'; 
      res[rind+2] = 'd';
      res[rind+3] = ' ';
      rind = rind + 4;
    } 
    else if (strcmp("String",tok) == 0)
    { res[rind] = '%'; 
      res[rind+1] = 's'; 
      res[rind+2] = ' ';
      rind = rind + 3;
    } 
    else if (strcmp("boolean",tok) == 0)
    { res[rind] = '%'; 
      res[rind+1] = 's'; 
      res[rind+2] = ' ';
      rind = rind + 3;
    } 
    else
    { res[rind] = '%'; 
      res[rind+1] = 'd'; 
      res[rind+2] = ' ';
      rind = rind + 3;
    } 
  } 
  res[rind] = '\0'; 
  return res; 
} 

